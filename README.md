# Projet d'application Mobile - Android

## API_app
Cette application peut s'utiliser seul et permet d'allumer/éteindre des devices d'une maison connectée via API

## Bluetooth_API_app
Cette application nécessite 2 mobiles (2 applications lancées) pour fonctionné. 
  - Un mobile en mode CLIENT -> Modifie les états des devices et envoie par bluetooth ces opérations au 2ième mobile
  - Un mobile en mode SERVEUR -> Reçoit les informations du client et modifie les états des devices grâce à l'API (Reprise du code API_app)
