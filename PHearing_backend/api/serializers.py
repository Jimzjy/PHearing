from rest_framework import serializers
from api.models import UserData, Image, AudiometryData, SpeechData, Navigation, News


class UserSerializer(serializers.ModelSerializer):

    class Meta:
        model = UserData
        fields = ('id', 'username', 'birthYear', 'phoneNumber', 'sex', 'deaf', 'hearingAid', 'cochlearImplant',
                  'avatar')


class UserChangeSerializer(serializers.ModelSerializer):

    class Meta:
        model = UserData
        fields = ('id', 'username', 'password', 'birthYear', 'phoneNumber', 'sex', 'deaf', 'hearingAid',
                  'cochlearImplant', 'avatar')

    def create(self, validated_data):
        user = UserData.objects.create_user(**validated_data)
        return user

    def update(self, instance, validated_data):
        instance.password = validated_data.get('password', instance.password)
        instance.birthYear = validated_data.get('birthYear', instance.birthYear)
        instance.phoneNumber = validated_data.get('phoneNumber', instance.phoneNumber)
        instance.sex = validated_data.get('sex', instance.sex)
        instance.deaf = validated_data.get('deaf', instance.deaf)
        instance.hearingAid = validated_data.get('hearingAid', instance.hearingAid)
        instance.cochlearImplant = validated_data.get('cochlearImplant', instance.cochlearImplant)
        instance.avatar = validated_data.get('avatar', instance.avatar)
        instance.save()
        return instance


class ImageSerializer(serializers.ModelSerializer):

    class Meta:
        model = Image
        fields = ('image',)


class AudiometryDataSerializer(serializers.ModelSerializer):

    class Meta:
        model = AudiometryData
        fields = ('id', 'createTime', 'rightResult', 'leftResult', 'rightData', 'leftData')


class SpeechDataSerializer(serializers.ModelSerializer):

    class Meta:
        model = SpeechData
        fields = ('id', 'createTime', 'score')


class NavigationSerializer(serializers.ModelSerializer):

    class Meta:
        model = Navigation
        fields = ('id', 'image', 'contentUrl')


class NewsSerializer(serializers.ModelSerializer):
    pubTime = serializers.DateTimeField(format='%Y-%m-%d %H:%M:%S')

    class Meta:
        model = News
        fields = ('id', 'pubTime', 'title', 'content')


class NewsListSerializer(serializers.HyperlinkedModelSerializer):

    class Meta:
        model = News
        fields = ('url', 'title', 'excerpt', 'image')
