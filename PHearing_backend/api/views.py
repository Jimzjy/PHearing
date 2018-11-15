from rest_framework import viewsets, mixins, status, filters
from rest_framework.response import Response
from rest_framework.decorators import action
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from api.models import UserData, Image, AudiometryData, SpeechData, Navigation, News
from api.serializers import UserSerializer, UserChangeSerializer, ImageSerializer, AudiometryDataSerializer,\
    SpeechDataSerializer, NavigationSerializer, NewsSerializer, NewsListSerializer
from api.permissions import IsUserOwnerOrAdmin, IsDataOwnerOrAdmin


class UserViewSet(mixins.CreateModelMixin,
                  mixins.RetrieveModelMixin,
                  mixins.UpdateModelMixin,
                  mixins.DestroyModelMixin,
                  viewsets.GenericViewSet):
    queryset = UserData.objects.all()
    serializer_class = UserSerializer
    permission_classes = (IsUserOwnerOrAdmin,)

    def create(self, request, *args, **kwargs):
        serializer = UserChangeSerializer(data=request.data, context={"request": request})
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)

    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        serializer = UserChangeSerializer(instance, data=request.data, partial=partial, context={"request": request})
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)

        if getattr(instance, '_prefetched_objects_cache', None):
            # If 'prefetch_related' has been applied to a queryset, we need to
            # forcibly invalidate the prefetch cache on the instance.
            instance._prefetched_objects_cache = {}

        return Response(serializer.data)

    @action(methods=['get'], detail=True, url_path='audiometry-data')
    def audiometry_data(self, request, *args, **kwargs):
        audiometry_data = self.get_object().audiometry_data
        serializer = AudiometryDataSerializer(audiometry_data, read_only=True, many=True)
        return Response(serializer.data)

    @action(methods=['get'], detail=True, url_path='speech-data')
    def speech_data(self, request, *args, **kwargs):
        speech_data = self.get_object().speech_data
        serializer = SpeechDataSerializer(speech_data, read_only=True, many=True)
        return Response(serializer.data)


class ImageViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Image.objects.all()
    serializer_class = ImageSerializer


class AudiometryDataViewSet(mixins.CreateModelMixin,
                            mixins.DestroyModelMixin,
                            viewsets.GenericViewSet):
    queryset = AudiometryData.objects.all()
    serializer_class = AudiometryDataSerializer
    permission_classes = (IsDataOwnerOrAdmin,)

    def perform_create(self, serializer):
        user_data = UserData.objects.get(username=self.request.user.username)
        serializer.save(owner=user_data)


class SpeechDataViewSet(mixins.CreateModelMixin,
                        mixins.DestroyModelMixin,
                        viewsets.GenericViewSet):
    queryset = SpeechData.objects.all()
    serializer_class = SpeechDataSerializer
    permission_classes = (IsDataOwnerOrAdmin,)

    def perform_create(self, serializer):
        user_data = UserData.objects.get(username=self.request.user.username)
        serializer.save(owner=user_data)


class NavigationViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Navigation.objects.all()
    serializer_class = NavigationSerializer

    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())
        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)


class NewsViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = News.objects.all()
    serializer_class = NewsSerializer
    filter_backends = (filters.SearchFilter,)
    search_fields = ('title',)

    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())

        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = NewsListSerializer(page, many=True, context={'request': self.request})
            return self.get_paginated_response(serializer.data)

        serializer = NewsListSerializer(queryset, many=True, context={'request': self.request})
        return Response(serializer.data)


class AuthToken(ObtainAuthToken):

    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data,
                                           context={'request': request})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        token, created = Token.objects.get_or_create(user=user)
        return Response({
            'token': token.key,
            'userId': user.pk,
        })
