from rest_framework import permissions


class IsUserOwnerOrAdmin(permissions.BasePermission):

    # only admin and owner can get the user data
    def has_object_permission(self, request, view, obj):
        return request.user and (obj.username == request.user.username or request.user.is_staff)


class IsDataOwnerOrAdmin(permissions.BasePermission):

    def has_permission(self, request, view):
        return request.user and request.user.is_authenticated

    def has_object_permission(self, request, view, obj):
        return request.user and (obj.owner.username == request.user.username or request.user.is_staff)
