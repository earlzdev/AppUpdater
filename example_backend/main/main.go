package main

import (
	"fmt"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"strconv"

	"github.com/gin-gonic/gin"
)

const (
	apkDir      = "apk_files"
	apkFilename = "app-debug.apk"
	versionFile = "apk_files/version.json"
	validToken  = "new_token"
)

type VersionInfo struct {
	VersionName string `json:"versionName"`
	VersionCode int    `json:"versionCode"`
	Checksum    string `json:"checksum"`
}

func calculateChecksum(filePath string) (string, error) {
	file, err := os.Open(filePath)
	if err != nil {
		return "", err
	}
	defer file.Close()

	hash := sha256.New()
	if _, err := io.Copy(hash, file); err != nil {
		return "", err
	}

	return hex.EncodeToString(hash.Sum(nil)), nil
}

func saveVersionInfo(version VersionInfo) error {
	file, err := os.Create(versionFile)
	if err != nil {
		return err
	}
	defer file.Close()
	encoder := json.NewEncoder(file)
	return encoder.Encode(version)
}

func loadVersionInfo() VersionInfo {
	var version VersionInfo
	file, err := os.Open(versionFile)
	if err == nil {
		defer file.Close()
		json.NewDecoder(file).Decode(&version)
	}
	return version
}

func verifyToken(c *gin.Context) {
	token := c.GetHeader("Authorization")
	fmt.Println("Received token:", token) // Выводим токен в консоль

	if token != "Bearer "+validToken {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid token"})
		c.Abort()
		return
	}
	c.Next()
}


func main() {
	r := gin.Default()

	if err := os.MkdirAll(apkDir, os.ModePerm); err != nil {
		panic(err)
	}

	r.GET("/", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{"message": "Welcome to APK Server. Use /download to get the latest APK."})
	})

	r.GET("/check_version", func(c *gin.Context) {
		version := loadVersionInfo()
		c.JSON(http.StatusOK, version)
	})

	r.GET("/version_info", func(c *gin.Context) {
		apkPath := filepath.Join(apkDir, apkFilename)
		if _, err := os.Stat(apkPath); os.IsNotExist(err) {
			c.JSON(http.StatusNotFound, gin.H{"error": "File not found"})
			return
		}
		version := loadVersionInfo()
		c.JSON(http.StatusOK, gin.H{
			"versionName": version.VersionName,
			"versionCode": version.VersionCode,
			"checksum":    version.Checksum,
			"apk":         apkFilename,
		})
	})

	r.GET("/download", verifyToken, func(c *gin.Context) {
		apkPath := filepath.Join(apkDir, apkFilename)
		if _, err := os.Stat(apkPath); os.IsNotExist(err) {
			c.JSON(http.StatusNotFound, gin.H{"error": "File not found"})
			return
		}
		c.File(apkPath)
	})

	r.POST("/upload", func(c *gin.Context) {
		file, err := c.FormFile("file")
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid file"})
			return
		}

		versionName := c.PostForm("versionName")
		versionCodeStr := c.PostForm("versionCode")

		versionCode, err := strconv.Atoi(versionCodeStr)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid versionCode"})
			return
		}

		apkPath := filepath.Join(apkDir, apkFilename)
		if _, err := os.Stat(apkPath); err == nil {
			os.Remove(apkPath)
		}

		src, err := file.Open()
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to open file"})
			return
		}
		defer src.Close()

		dst, err := os.Create(apkPath)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create file"})
			return
		}
		defer dst.Close()

		if _, err = io.Copy(dst, src); err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to save file"})
			return
		}

		checksum, err := calculateChecksum(apkPath)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to calculate checksum"})
			return
		}

		version := VersionInfo{VersionName: versionName, VersionCode: versionCode, Checksum: checksum}
		saveVersionInfo(version)

		c.JSON(http.StatusOK, gin.H{"message": "File uploaded successfully"})
	})

	r.Run(":8000")
}