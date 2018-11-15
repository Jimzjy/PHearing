from django.db import models
from django.contrib.auth.models import User


class UserData(User):
    phoneNumber = models.CharField(max_length=20)
    birthYear = models.IntegerField()
    # true: male, false: female
    sex = models.BooleanField()
    deaf = models.BooleanField()
    hearingAid = models.BooleanField()
    cochlearImplant = models.BooleanField()
    avatar = models.ImageField(blank=True)

    class Meta(User.Meta):
        ordering = ('-id',)


class News(models.Model):
    pubTime = models.DateTimeField(auto_now=True)
    title = models.CharField(max_length=50)
    excerpt = models.CharField(max_length=50)
    content = models.TextField()
    image = models.TextField(blank=True)

    class Meta:
        ordering = ('-pubTime',)


class Navigation(models.Model):
    image = models.ImageField()
    contentUrl = models.TextField(blank=True)


class Image(models.Model):
    image = models.ImageField()


class AudiometryData(models.Model):
    createTime = models.BigIntegerField()
    rightResult = models.FloatField()
    leftResult = models.FloatField()
    rightData = models.TextField()
    leftData = models.TextField()
    owner = models.ForeignKey('UserData', related_name='audiometry_data', on_delete=models.CASCADE)


class SpeechData(models.Model):
    createTime = models.BigIntegerField()
    score = models.IntegerField()
    owner = models.ForeignKey('UserData', related_name='speech_data', on_delete=models.CASCADE)
