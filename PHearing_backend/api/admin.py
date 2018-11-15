from django.contrib import admin
from api.models import Image, News, Navigation, UserData, AudiometryData, SpeechData

admin.site.register(Image)
admin.site.register(News)
admin.site.register(Navigation)
admin.site.register(UserData)
admin.site.register(AudiometryData)
admin.site.register(SpeechData)
