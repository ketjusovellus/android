# Ketjun Android-sovellus

[In English](#ketju-android-application)

## Konteksti

Ketju-pilottiprojektin Android-sovelluksen lähdekoodi.

## Esittely

Ketjun Android-sovellus on [COVID-19](https://fi.wikipedia.org/wiki/COVID-19)-altistumisilmoitussovellus, joka käyttää [DP3T-SDK](https://github.com/DP-3T/dp3t-sdk-android)-kirjastoa lähietäisyydellä olevien toisten sovelluskäyttäjien havainnointiin Bluetooth-teknologian avulla. Ketju-pilotti käyttää DP3T-kehittäjien alkuperäistä Bluetooth-protokollaa ennen [Googlen](https://www.google.com/covid19/exposurenotifications/) ja [Applen](https://developer.apple.com/documentation/exposurenotification) julkaiseman korvaavan Exposure Notification -rajapinnan käyttöönottoa.

![image](./readme-screenshot.png)

## Tarkoitus

Ketju-pilottiprojektin tarkoitus on testata DP3T:n Bluetooth-protokollan soveltuvuutta ja tarkkuutta lähikontaktien määrittämisessä. Tämä sovellus ei ole tuotantovalmis.

## Käyttöönotto

### DP3T-SDK-alimoduulin kloonaus

1. Kloonaa DP3T-alimoduulin git-säilö (engl. *repository*)
2. Alusta DP3T-SDK-alimoduuli komennolla `git submodule init`
3. Aja alimoduulin päivityskomento `git submodule update --remote`
4. Mikäli haluat käyttää [muokattua DP3T-SDK-kirjastoa](#modifications-of-dp3t-sdk), valitse oikea git-haara komennoilla `cd dp3t-sdk-android && git checkout ketju-pilot`

Kääntääksesi ja ajaaksesi Ketju-sovelluksen, muutamalle projektin ympäristömuuttujalle on määriteltävä ensin arvot. Luo `app/gradle.properties` -tiedosto ja lisää sinne seuraavat rivit:

```groovy
KEYSTORE_PASSWORD=
KEY_PASSWORD=

KETJU_BACKEND_URL_DEV=""
KETJU_BACKEND_URL_QA=""
KETJU_BACKEND_URL_PROD=""
KETJU_BACKEND_CERTIFICATE_HASH_DEV=""
KETJU_BACKEND_CERTIFICATE_HASH_QA=""
KETJU_BACKEND_CERTIFICATE_HASH_PROD=""
KETJU_BACKEND_JWT_PUBLIC_KEY_DEV=""
KETJU_BACKEND_JWT_PUBLIC_KEY_QA=""
KETJU_BACKEND_JWT_PUBLIC_KEY_PROD=""
```

Sovellukseen liittyy kolme oleellista ympäristömuuttujaa:

| Ympäristömuuttuja                    | Kuvaus                                           |
|--------------------------------------|--------------------------------------------------|
| `KETJU_BACKEND_URL`                  | DP3T-taustajärjestelmän osoite                   |
| `KETJU_BACKEND_JWT_PUBLIC_KEY`       | DP3T-taustajärjestelmän vaatima julkinen avain   |
| `KETJU_BACKEND_CERTIFICATE_FILENAME` | Käytetystä varmenteesta laskettu SHA256 tiiviste |

Nämä ympäristömuuttujat on valmiiksi eriytetty `gradle.properties`-tiedostossa kolmea erilaista ajoympäristöä varten: kehitysversio (*dev*), laadunvarmistus (*qa*) ja tuotantoversio (*prod*).

Muokkaa näitä ympäristömuuttujia siten, että ne vastaavat käytetyn taustajärjestelmän konfiguraatiota. `KETJU_BACKEND_URL` on edellä mainituista muuttujista ainoa täysin pakollinen. Kuitenkin *sairastumisilmoituserien* (engl. *exposure buckets*) noutaminen DP3T Android SDK:lla vaatii oikein toimiakseen myös määrittelyn `KETJU_BACKEND_JWT_PUBLIC_KEY`:ll, jonka tulee olla välitetty DP3T SDK:lle. Tämän lisäksi taustajärjestelmätoteutuksen tulee tukea DP3T SDK:n tekemien verkkokutsujen allekirjoitusta vastaavalla salaisella avaimella.

### Varmenteen kiinnitys

Käyttääksesi varmenteen kiinnitystä (engl. *certificate pinning*), laske SHA256-tiiviste taustajärjestelmän käyttämästä varmenteesta ja sijoita se tekstimuotoisena `KETJU_BACKEND_CERTIFICATE_HASH`-ympäristömuuttujaan (esimerkiksi: `KETJU_BACKEND_CERTIFICATE_HASH_DEV="sha256/varmenteenTiivisteSummaTässä=`). Mikäli kyseisen ympäristömuuttujan arvo jätetään tyhjäksi, varmennetta ei hyödynnetä verkkoliikenteessä.

## Riippuvuudet

- [DP3T-SDK for Android](https://github.com/ketjusovellus/dp3t-sdk-android)
- [Lottie](https://github.com/airbnb/lottie-android)

Kaikki sovelluksen käyttämät riippuvuudet on lisätty Gradle Package Manager -pakettienhallintatyökalulla.

## Muutoksista DP3T-SDK-kirjastoon

Ketju-pilotin testivaihetta varten [DP3T-SDK-kirjastoon](https://github.com/DP-3T/dp3t-sdk-android) on tehty [muutoksia](https://github.com/ketjusovellus/dp3t-sdk-android), joilla tallennetaan tarkempaa tietoa *kohtaamisista (eng. contact)* ja *kättelyistä (eng. handshake)* sovelluksen sisäiseen kohtaamistietokantaan. Näitä tarkempia tietoja hyödyntämällä voidaan arvioida testijakson aikana Bluetoothilla kerättyjä kohtaamistietojen luotettavuutta vertailemalla niitä testikäyttäjien manuaalisesti kirjaamiin kohtaamisiin.

Jotta testikäyttäjien kohtaamiset saadaan tunnistettua ja yksilöityä, DP3T-SDK-kirjastoon on tehty olennainen muutos Bluetooth-kättelyiden yhteydessä laitteiden välillä vaihdettaviin *lyhytaikaistunnisteisiin (eng. ephemeral identifier)*, joihin on lisätty neljän tavun mittainen etuliite. Tämä voi olla muodostettu esimerkiksi testihenkilön nimikirjaimista tai olla testihenkilöä kohden satunnaisesti valittu. Toisin kuin DP3T-SDK:n normaalissa anonyymissa toimintatilassa, etuliitteellinen lyhytaikaistunniste on pilotissa yhdistettävissä tiettyyn testikäyttäjään, jolloin kerätyt kohtaamistiedot testihenkilöiden välillä voidaan yhdistää toisiinsa jälkikäteen. Yhdistelemällä tietoa kohtaamisen alku- ja loppuajasta sekä lasketusta etäisyydestä voidaan datasta luoda erilaisia visualisaatioita, kuten aikajanoja eri testihenkilöiden kohtaamisista, ja näin vertailla sovelluksen avulla kerättyä ja manuaalisesti kirjattua kohtaamisdataa keskenään.

Lisäykset *kohtaamistietoihin* (*contacts* -tauluun):

- Etuliite lyhytaikaistunnisteeseen (*ephemeral identifier*)
- Kohtaamisen alku- ja loppuaika pelkän päiväystiedon lisäksi
- Kohtaamisen kesto
- Signaalin keskimääräinen vaimenema kohtaamisen ajalta
- Signaalin vaimenemasta laskettu keskimääräinen etäisyys kohtaamisen ajalta

Lisäykset *kättelytietoihin* (*handshakes*-tauluun)

- Etuliite lyhytaikaistunnisteeseen (*ephemeral identifier*)
- Etäisyys, joka on laskettu ilmoitetusta lähetystehosta (TX power) ja vastaanotetusta signaalin voimakkuuden mittaustuloksesta (RSSI).

Nämä yllä mainitut muokkaukset ovat tarkoitettu **ainoastaan** testikäyttöä ja Bluetooth-jäljitysprotokollan toimivuuden varmistamista varten, eikä näitä muokkauksia tule **milloinkaan** sisällyttää sovelluksen tuotantoversioon.

# Ketju Android application

[Suomeksi](#ketju-android-sovellus)

## Context

The Android application source code of Ketju Pilotti project.

## Introduction

Ketju Android is a [COVID-19](https://en.wikipedia.org/wiki/Coronavirus_disease_2019) exposure notification mobile application, using [DP3T-SDK](https://github.com/DP-3T/dp3t-sdk-android) library for proximity tracing over Bluetooth to detect close contacts between other application users. Ketju Pilotti uses the initial Bluetooth protocol introduced by the DP3T team, prior to the introduction and their adaptation to use the new Exposure Notification API by [Google](https://www.google.com/covid19/exposurenotifications/) and [Apple](https://developer.apple.com/documentation/exposurenotification) instead.

![image](./readme-screenshot.png)

## Purpose

The purpose of Ketju Pilotti project is to test feasibility and measure accuracy of contact detection with DP3T Bluetooth protocol. This is not a production ready application.

## Setup

### Cloning with DP3T SDK submodule

1. Clone the repository
2. Initialize DP3T SDK submodule with `git submodule init`
3. Update the submodule `git submodule update --remote`
4. If you wish to use [Ketju customized DP3T SDK](#modifications-of-dp3t-sdk), select the correct branch with `cd dp3t-sdk-android && git checkout ketju-pilot`

### Environment variables

In order to successfully build & run Ketju, you need to have some environment variables in place. Add them by creating a file `app/gradle.properties` and add the following variables in it:

```groovy
KEYSTORE_PASSWORD=
KEY_PASSWORD=

KETJU_BACKEND_URL_DEV=""
KETJU_BACKEND_URL_QA=""
KETJU_BACKEND_URL_PROD=""
KETJU_BACKEND_CERTIFICATE_HASH_DEV=""
KETJU_BACKEND_CERTIFICATE_HASH_QA=""
KETJU_BACKEND_CERTIFICATE_HASH_PROD=""
KETJU_BACKEND_JWT_PUBLIC_KEY_DEV=""
KETJU_BACKEND_JWT_PUBLIC_KEY_QA=""
KETJU_BACKEND_JWT_PUBLIC_KEY_PROD=""
```

There are 3 important variables:

| Variable                             | Description                                       |
|--------------------------------------|---------------------------------------------------|
| `KETJU_BACKEND_URL`                  | Base URL of the DP3T backend                      |
| `KETJU_BACKEND_JWT_PUBLIC_KEY`       | Public key string for the DP3T backend            |
| `KETJU_BACKEND_CERTIFICATE_HASH`     | SHA256 hash from the certificate used for pinning |

The dev, qa and production versions of these environment variables have been split by default and can be variated within the `gradle.properties` -file.

Modify these variables to reflect your backend configuration. `KETJU_BACKEND_URL` is only mandatory information, although DP3T Android SDK will *only* fetch new exposure buckets correctly, if the `KETJU_BACKEND_JWT_PUBLIC_KEY` is defined and the backend implements support for it.

### Certificate pinning

To enable certificate pinning, get the certificate's SHA256 hash and place it in `KETJU_BACKEND_CERTIFICATE_HASH` (example: `KETJU_BACKEND_CERTIFICATE_HASH_DEV="sha256/sthSthYaddaYadda=`. If you leave the value empty, certificate pinning will be disabled.

## Notable dependencies

- [DP3T-SDK for Android](https://github.com/ketjusovellus/dp3t-sdk-android)
- [Lottie](https://github.com/airbnb/lottie-android)

All dependencies are added by the Gradle package manager.

## Modifications of DP3T-SDK

A custom [fork](https://github.com/ketjusovellus/dp3t-sdk-android) of [DP3T-SDK](https://github.com/DP-3T/dp3t-sdk-android) is used in Ketju Pilotti test phase to store more detailed *contact* and *handshake* information into the internal contact database. An export of this database file can be used afterwards to validate the generated contact data with the actual documented real life contacts between the application test users.

A custom prefix identifier, like initials of a test user, can be set and used on the frequently changing *ephemeral identifiers* of the user, used in the contact data exchange of the Bluetooth protocol. Unlike in the normal anonymous operation, the prefixed *ephemeral identifiers* **can** be directly linked to individual test users and thus be used to generate contact timelines between different test users and hence to verify the data with actual observations.

Extensions of *contact* data:

- Custom prefix identifier
- Start and end time, in addition to date
- Contact duration
- Mean signal attenuation over the contact
- Mean distance, calculated from the mean attenuation, over the contact

Extensions of *handshake* data:

- Custom prefix identifier
- Distance, calculated from the TX power level and RSSI

These modifications are meant to be used **only** for verification purposes of the Bluetooth proximity tracing protocol in a testing phase and to **never** included in any production release.
