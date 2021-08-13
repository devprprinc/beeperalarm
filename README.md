# OPIS

BeeperAlarm je android aplikacija, ki ob prejemu SMS sporočila sproži alarm.

## PRENOS

Najnovejša verzija aplikacije (trenutno beta verzija) za prenos je na volju tu:

[**BeeperAlarm-v1-1.0.0-beta.apk**](https://github.com/devprprinc/beeperalarm/releases/download/v1-1.0.0-beta/BeeperAlarm-v1-1.0.0-beta.apk)


### Funkcije
* filtriranje preko pošiljateljeve številke ali vsebujoče besede v SMS sporočilu
* nastavljenja več filtrov
* nastavljanje melodije alarma za posamezni filter
* pripomoček na namizni strani za hiter vklop/izklop aplikacije


## Kratka navodila
Ta aplikacija aktivira alarm in odpre okno z SMS vsebino, ob prejemu posebnega SMS sporočila. Za aktivacijo alarma preko SMS sporočila je potrebno vnesti pošiljateljevo številko ali ključno besedo v SMS sporočilu (ni potrebno oboje).
Pri vnosu številke je lahko vnesen samo del številke, vendar minimalno 3 številke. Pri vnosu ključne besede je potrebno vnesti minimalno 3 črke, v primeru večih ključnih besed jih loči z znakom ";".
Alarm se aktivira tudi v načinu "Ne moti". Za hitro izklapljanje/vklapjanje alarma preko "začetnega zaslona" je razvit "pripomoček".
Za delovanje aplikacije je potrebno imeti naslednja dovoljenja, ki jih nastaviš preko sistemskih nastavitev telefona:
* SMS (branje vseh sporočil)
* Datoteke in predstavnost (shranjevanje zgodovine sporočil)
* Stiki (za povezovanje pošiljateljevo številko s shranjenim kontaktom)
* Prekrivanje drugih aplikacij (za aktiviranje okna ob aktivaciji alarma).

Prav tako je potrebno izklopiti možnost "Odstrani dovoljenja, če aplikacija ni v uporabi".

:warning:   Pri nekaterih proizvajalcih mobilnih telefonov (Samsung, Huawei ...) je potrebno tudi aplikaciji onemogočiti nastavitev "optimizacija baterije" oz. podobno.



## Dovoljenja aplikacije
Za delovanje aplikacija potrebuje naslednja dovoljenja:
* SMS (branje vseh SMS sporočil)
* Stiki (za lepši izpis pošiljatelja)
* Prekrivanje drugih aplikacij (za avtomatski prikaz okna ob alarmu)

Za stabilno delovanje se priporoča tudi odstranitev nastavitve "Odstrani dovoljenja, če aplikacija ni v uporabi".

:warning:    Pri nekaterih proizvajalcih mobilnih telefonov (Samsung, Huawei ...) je potrebno tudi aplikaciji onemogočiti nastavitev "optimizacija baterije" oz. podobno.
Aplikacija je razvita za android 10 in višje in ne deluje na napravah z "Go edition" OS.


[<img src="screenshots/dovoljenje1.png" width=300>](screenshots/dovoljenje1.png)
[<img src="screenshots/dovoljenje2.png" width=300>](screenshots/dovoljenje2.png)


### Zaslonske slike

Zaslonske slike aplikacije:

[<img src="screenshots/zaslon0.png" width=300>](screenshots/zaslon0.png)
[<img src="screenshots/zaslon1.png" width=300>](screenshots/zaslon1.png)
[<img src="screenshots/zaslon2.png" width=300>](screenshots/zaslon2.png)
[<img src="screenshots/zaslon3.png" width=300>](screenshots/zaslon3.png)
[<img src="screenshots/zaslon4.png" width=300>](screenshots/zaslon4.png)
[<img src="screenshots/widget.gif" width=300>](screenshots/widget.gif)

## Licenca
BeeperAlarm je prosta in odprta programska oprema: lahko jo uporabljaš, preučuješ in izboljšaš po želji. Lahko jo distributiraš in/ali spremeniš pod pogoji [GNU General Public License](https://www.gnu.org/licenses/gpl.html), kot jo je izdala Free Software Foundation v različici 3 ali po izbiri v katerikoli novejši različici.

© Klemen Rahne, 2021

### Zunanje knjižnice


Slide To Act (Nicola Corti, licence MIT) https://github.com/cortinico/slidetoact


### Zasebnost
Aplikacija ne zbira in ne pošilja nobenih uporabnikovih podatkov (ne zahteva dostopa do internetne povezave). Aplikacija shranjuje samo zgodovino prejetih alarmov, kateri se ne delijo naprej.

## Ostalo

V primeru napak, novih funkcionalnosti ali nedelovanje aplikacije piši na: dev.prprinc@gmail.com

Ta aplikacija je zastonj in brez oglasov.

