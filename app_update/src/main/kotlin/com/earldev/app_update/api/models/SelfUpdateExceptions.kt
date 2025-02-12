package com.earldev.app_update.api.models

class NoInstallPermissionException : Exception()

class NoNeedToUpdateException : Exception()

class DeclinedToGivePermissionException : Exception()

class CancelledInstallationException : Exception()

class UnsecuredApkException : Exception()

class UnauthorizedException : Exception()