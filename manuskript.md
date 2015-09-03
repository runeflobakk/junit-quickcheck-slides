> _husk klikk "neste" for å starte presentasjon!_

Hei!

Er det mange her som **liker enhetstesting og testdrevet utvikling**? (hold opp hender).

--------------------------------


Jeg heter Rune, og jeg jobber som **utvikler** i BEKK, og jeg _digger_ enhetstester.
 - de er **raske**
 - jeg kan gjøre masse **detaljsjekking** at koden gjør som den skal, og det gir meg trygghet.
 - i tillegg hjelper de meg å lage **god løskoblet kode**.

----------------------------------


Jeg mener at **TDD ikke har dødd**, men det har kanskje mista litt av "edgen" sin. Det er definitivt _ikke hipt
og kult_ lenger, og det er **kanskje lett å slå seg litt til ro med å skrive noen asserts som man alltid gjør, og call it a day**.
Jeg mener at, akkurat som med programmering generelt, bør vi _anstrenge_ oss for å **stadig bli bedre til å skrive gode enhetstester**.

Det finnes masse verktøy for å skrive enhetstester, og jeg skal **vise dere enda et** som dere kan legge til
i **test-verktøykassa** deres.

-----------------------------------


**junit-quickcheck** er en utvidelse til JUnit, og tilbyr en litt **annen måte å tenke på test-input og forventede resultater**. Med junit-quickcheck beskriver man mer **generelle egenskaper** i stedet for å bruke konkrete eksempler som input og forventde resultater. junit-quickcheck er **inspirert av tilsvarende verktøy** for f.eks. Haskell og Scala.




------------------------------------


De fleste er godt kjent med å skrive **enhetstester som et sett med statiske eksempler som skal gi et fast resultat**.
God praksis er å inkludere **corner-cases**, og jo flere eksempler man lager, desto tryggere kan man føle seg
at koden takler alt man kaster på den.


-------------------------------------


Dersom du skulle implementere en **metode som reverserer en String** (_for å ta et eksempel som alle kan kjenne
seg igjen i :wink:_), hvilke tester ville du skrive for å bevise at metoden gjør det den skal **for alle Strings**?


- sjekke at `reverse("abc") == "cba"`
- sjekke at `reverse("xx") == "xx"`
- sjekke at `reverse("x") = "x"`
- sjekke at `reverse("") == ""`
- mer?

Testene beskrevet over gir _60% testdekning_ av Java sin egen implementasjon av reverse, som ligger i `StringBuilder.reverse()`.

-------------------------------------

Den ser slik ut. Jeg vet ikke hva alt her gjør for noe en gang. **Hvordan skal vi greie å teste** at all denne koden faktisk gjør som den skal?

-------------------------------------

En _annen måte å beskrive reversering_ av Strings er slik:

> **Reversering av sammensetningen av to vilkårlige stringer,**

f.eks. `reverse("abc" + "def")`

> **er det samme som reversering av hver streng, satt sammen
> i motsatt rekkefølge.**

Altså `reverse("def") + reverse("abc")`

-------------------------------------------
Dette stemmer for alle strenger.

-------------------------------------------

Og denne definisjonen kan fungere _som en test skrevet v.h.a. junit-quickcheck_:

```java
@Theory
public void reversering(@ForAll String s1, @ForAll String s2) {

    assertThat(reverse(s1 + s2), is(reverse(s2) + reverse(s1)));

}
```

Denne testen gir **100% testdekning** av Java sin implementasjon av `StringBuilder.reverse()`.

Her sier vi at denne testen er gyldig for _hva som helst av Stringene **s1** og **s2**_, og junit-quickcheck vil **kalle testmetoden gjentatte ganger med tilfeldige Strings av ulik lengde**, også tomme strenger, for å sjekke om det vi påstår virkelig er sant.

I tillegg er testmetoden markert som en `@Theory` i stedet for den vanlig `@Test`-annotasjonen

---------------

samt at vi instruerer JUnit til å benytte `Theories`-runner'en til å kjøre testene i stedet for den vanlige runneren. Du kan likevel ha tradisjonelle tester annotert med `@Test` i samme testklasse dersom du vil teste spesifikke cases.


------------------


#### OCSP-lookup som gir annen responskode enn 200

Vi skal se på et litt mer **relevant eksempel**.

_Hvor mange her har hørt om OCSP?_

Online Certificate Status Protocol er protokollen for en tjeneste man kan spørre om **i utgangspunktet gyldige sertifikater har blitt revokert**, altså av ulike årsaker blitt **ansett som ugyldige**. F.eks. hvis sertifikatet har kommet på avveie, eller andre årsaker. OCSP er en **vanlig del av valideringsprosessen** av sertifikater. En OCSP-tjeneste er et vanlig **HTTP-endepunkt** man sender en **request** til og får en **respons** tilbake som **angir om sertifikatet er revokert eller ikke**.

-------------

```java
@Theory
public void ocspLookupIsUndecidedForAnythingButStatusCode200(
@ForAll @InRange(min="100", max="599") int otherThan200) {
  assumeThat(otherThan200, not(200));

  given(ocspResponseStatus.getStatusCode()).willReturn(otherThan200);

  assertThat(certValidator.validate(certificate), is(UNDECIDED));
}
```


Her skal vi teste at vi anser alle andre HTTP respons-koder enn 200 fra OCSP-tjenesten som "UNDECIDED", og vi trenger ikke å velge en eller flere vilkårlige responskoder som eksempler på dette.

Vi spesifiserer at "for alle tall mellom 100 og 599, og vi antar at det ikke er 200, gitt at statuskoden i OCSP-responsen er dette tallet, vil valideringen av sertifikatet resultere i "UNDECIDED".

`assumeThat()` er en standard JUnit-metode som brukes til å avbryte en testkjøring dersom en tilstand ikke er oppfylt, men uten å feile testen.

------------------------------------------

# Oppsummering

junit-quickcheck kan være relevant for testing av
- Forretningsregler
- Sikkerhet
- Redusering eller kategorisering av en mengde verdier til et fåtall andre verdier
- algoritmer med "matematiske" definisjoner, a la strengreversering, men dette kan være vanskelig.

------------------------------------------

- tillegg til vanlige enhetstester
- vurder å prøve det dersom verdier man velger å teste med føles kunstige, og kanskje ikke dekker hele spekteret man egentlig skal teste.
- Det jeg liker best ved det er at det utfordrer meg til å tenke litt annerledes m.t.p. testing, og som utvikler er dette veldig sunt!

--------------------------------------

Takk for meg!
