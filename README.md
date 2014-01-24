moves2taltioni
==============

Transfer data from [Moves](https://www.moves-app.com/) to [Taltioni](http://www.taltioni.fi/en)

Please note that a REST API is also in the works for Taltioni!
You might like that one better than the SOAP API used in this example. Please check it out at https://rest.taltioni.fi/Help

Instructions for getting started below (in Finnish).


Asennusohjeet:

- Tuo projekti Eclipseen (valikosta File -> Import... -> General / Existing Projects into Workspace).
  
- Kun projekti tuotu, k‰‰nn‰ se. Valitse projekti, sitten valikosta Project  -> Clean... -> (o) Clean projects selected below ja [v] Moves2Taltioni.
  Alla viel‰ [v] Start build immediately ja (o) Build only the selected projects. 

- Kun projekti k‰‰nnetty, voidaan ajaa se. 
  Navigaattorissa Java Resources / src / com.sensotrend.servlet / MovesAuthenticationServlet.java. 
  Valikosta Run -> Run as -> Run on Server.

- Jos konfiguraatiota ei ole viel‰ valittu, aukeaa valikko, jossa saa konfiguroida. "Manually define a new server."
  Valittuna on Basic -> J2EE Preview. Vaihda Apache -> Tomcat v7.0. Palvelin saa olla localhost. Lis‰ksi valitse "Always use this server when running this project".
  Next -> 
  Jos koneelle on jo Tomcat asennettu, saat selata asennuspolun laatikkoon Tomcat installation directory:. Muussa tapauksessa Download and Install.
  Next -> 
  Seuraavaksi raahataan 'Moves2Taltioni' oikealle, jos ei ole jo. Ja ei kun menoksi.

- Tomcatin asennusprosessi luo oheistuotteenaan Servers-hakemiston workspaceen.

- Palomuuri saattaa varmistaa javan ajelun asianmukaisuutta. Salli. Vaikka myˆs julkisissa verkoissa.

- Eclipsen sis‰‰n pit‰isi k‰ynnisty‰ selain, joka koittaa yhteytt‰ osoitteeseen http://localhost:8080/Moves2Taltioni/Taltioni.
  Tuolta l‰hdet‰‰n ensin edelleenohjauksella Taltioniin tunnistautumaan. Valitse Fujitsu Testi TUPAS, p‰‰set syˆtt‰m‰‰n henkilˆtiedot, luomaan uuden Taltioni-tilin (ellei kyseisell‰ k‰ytt‰j‰ll‰ sellaista Taltionin asiakastesti-ymp‰ristˆss‰ jo ole) ja sen j‰lkeen viel‰ liitt‰m‰‰n palvelun k‰ytt‰j‰lle.
  
  Prosessin j‰lkeen p‰‰st‰‰n kytkem‰‰n Moves-palvelu. Onnistuneen liitoksen j‰lkeen esiin tulee sivu, jossa olevan linkin kautta voi siirt‰‰ Moves-sovelluksen tiedot Taltioni-tilille.
  
- Jotta SOAP-kutsut Taltionin testipalveluun toimisivat, pit‰‰ hakea selaimella varmenne osoitteesta https://asiakastestipalvelut.taltioni.fi:9443/Taltioni
  ja tallentaa se luotettuihin varmenteisiin (keytool -import -v -trustcacerts -alias taltioni -file c:\Users\*user*\Desktop\asiakastestipalvelut.taltioni.fi.crt -keystore "c:\Program Files\Java\jre7\lib\security\cacerts" -keypass changeit -storepass changeit
  Ja tuon j‰lkeen pit‰‰ viel‰ varmistaa, ett‰ Tomcat (tai Eclipse) oikeasti k‰ytt‰‰ tuota JRE-asennusta.
