package com.earldev.app_update.api.models

/**
 * Exception thrown when the app lacks permissions to install packages.
 */
class NoInstallPermissionException : Exception()

/**
 * Exception thrown when an update is not required.
 */
class NoNeedToUpdateException : Exception()

/**
 * Exception thrown when the user declines to grant permissions for installing packages.
 */
class DeclinedToGivePermissionException : Exception()

/**
 * Exception thrown when the update installation is canceled.
 */
class CancelledInstallationException : Exception()

/**
 * Exception thrown when the APK file is considered unsafe.
 */
class UnsecuredApkException : Exception()

/**
 * Exception thrown when a 401 error is received from the server on any request.
 */
class UnauthorizedException : Exception()
