EasyParking Android mugikorreko aplikazioa da, erabiltzaileari aparkalekuen egoera denbora errealean erakusteko diseinatua.
Aplikazioak OSMDroid + OpenStreetMap, Firebase Firestore eta Firebase Auth erabiltzen ditu.

🟢 1. Zer da EasyParking?

EasyParking-ek erabiltzaileari laguntzen dio aparkaleku libreak aurkitzen eta bere kotxea zein gunetan utzi duen erregistratzen.
Aparkaleku eremu bakoitza (sektorea) poligono baten bidez bistaratzen da mapan.

Aplikazioak honako aukera hauek eskaintzen ditu:

✔ Aparkaleku guneak mapan erakutsi
✔ Zenbat plaza libre dauden jakin
✔ Datuak automatikoki eguneratu Firestoretik
✔ Erabiltzaileak zein gunean aparkatzen duen gordetzea
✔ Mapan erdiguneko mira bidez gunea automatikoki antzematea
✔ Erabiltzailearen posizio erreala erakustea (GPS)
✔ Aparkatzeko prozesua erraztea

🔵 2. Mapa (OSMDroid + OpenStreetMap)

Maparen ezaugarriak:

✔ Zum eta scrolla
✔ Erabiltzailearen posizioa puntutxo urdin batekin
✔ Zehaztasun-zirkulua
✔ Mapan finko geratzen den mira (“fixed center mode”)
✔ Sektoreen poligonoak eta etiketak

🟠 3. Firebase Firestore datu-egitura

Aplikazioak bi kolekzio nagusi erabiltzen ditu:

🟣 Sektoreak

Elementuak:

izena

edukiera

koordenatuak (poligonoaren puntuak)

usuarioId

libres (plaza libreak, automatikoki kalkulatzen direnak)

🔵 Kotxe aparkalekuak

Elementuak:

usuarioId

zona (aparkatutako gunea)

Honen bidez kalkulatzen da plaza libreen kopurua:

libres = capacidad - coches_en_esa_zona

🟤 4. Sektoreen bistaratzea (poligonoak)

Sektore bakoitza honela marrazten da:

poligono urdin argia

ertz urdina

erdian testuzko etiketa (plaza libreak)

Klik egiten denean:

Poligonoa nabarmendu egiten da

Erabiltzaileak hautatutako gunea gordetzen da

Firestore-n aparkatzea erregistratzen da

ZoneInfoFragment eguneratzen da

Mapa sektore horretara animatzen da

Zoom automatikoa egiten da

🔴 5. Mapa erdiko mira finkoa (Fixed Center Mode)

Aplikazioaren funtzionalitaterik bereziena.

Botoia aktibatzen denean:

✔ Ikono bat jartzen da maparen erdian
✔ 40m inguruko zirkulua sortzen da
✔ Mapa mugitu ahala:

mira beti erdian mantentzen da

zirkulua eguneratzen da

mira azpian dagoen sektorea automatikoki detektatzen da

sektorearen clickListener-a exekutatzen da

Honek aparkalekua aukeratzeko zehaztasuna handitzen du, poligonoa ukitu beharrik gabe.

🟣 6. Erabiltzailearen kokapena (GPS)

Aplikazioak kokapen-baimena eskatzen du eta:

✔ FusedLocationProviderClient erabiltzen du
✔ Kokapena 2–4 segundoro eguneratzen du
✔ Puntutxo urdina mugituz erakusten du
✔ Zehaztasun-zirkulua marrazten du
✔ Lehen kokapenarekin mapa erdira eramaten du


🟢 7. Denbora errealeko eguneraketak

Firestoreko “sectores” aldatu ahala:

norbaitek aparkatzen duenean

plaza bat askatzen denean

edukiera aldatzen denean

→ Mapa berehala eguneratzen da.

Hau dela eta aplikazioa kolaboratiboa eta sinkronizatua da.

🟠 8. Elkarreragina eta interfazea

Aplikazioan badaude:

Mapa nagusia

Informazio panel txiki bat (ZoneInfoFragment)

Mira finkoa aktibatzeko botoia

Kolore eta efektu argiak erabiliz diseinatutako interfazea

🟣 9. EasyParking-ek zer arazo konpontzen du?

✔ Aparkalekua aurkitzeko denbora laburtzen du
✔ Gune bakoitzean zenbat plaza libre dauden jakinarazten du
✔ Erabiltzailearen aparkatzeak erregistratzen ditu
✔ Datuak denbora errealean eguneratzen dira
✔ Maparen bidez esperientzia erraza eta bisuala eskaintzen du
✔ Ez du hardware fisikorik behar (sensorik, barriketarik)

🟢 ONDORIOA

EasyParking aplikazio osoa da, eta honakoak uztartzen ditu:

OpenStreetMap bidezko mapa interaktiboak

Firestore bidezko datu partekatuak

Erabiltzailearen jarraipena GPS bidez

Denbora errealean plaza libreen kalkulua

Mira finkoa sektoreak automatikoki antzemateko

Erabiltzailearentzat oso intuitiboa eta erabilerraza da, eta sistemak ez du API garestirik behar.
