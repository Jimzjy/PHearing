from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from rest_framework.routers import DefaultRouter
from api import views

router = DefaultRouter()
router.register(r'users', views.UserViewSet)
router.register(r'images', views.ImageViewSet)
router.register(r'audiometry-data', views.AudiometryDataViewSet)
router.register(r'speech-data', views.SpeechDataViewSet)
router.register(r'navigation', views.NavigationViewSet)
router.register(r'news', views.NewsViewSet)

urlpatterns = [
    path('', include(router.urls))
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
