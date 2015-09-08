class: center, inverse
count: false

# Spesifisering av egenskaper med

# .large[junit-.red[quickcheck]]

---





class: center, inverse

# Spesifisering av egenskaper med

# .large[junit-.red[quickcheck]]

???

Hei!
Liker enhetstesting? (hold opp hender)


---

# Litt om meg:

- Rune Flobakk

- utvikler i BEKK

- jobbet i ca 8 år på ulike Java-prosjekter

- enhetstester! .large.red[❤]


???

Rune, BEKK, digger enhetstester.
- raske
- detaljsjekke
- god løskoblet kode.


---

class: center, inverse

# Men
# .huge[.red[TDD]]
# .large[var ikke død!]

???

- TDD har ikke dødd
- mista litt "edge".
- lett å slå seg til ro med kompetansen man har
- bli stadig bedre!

- finnes mange verktøy
- vise enda ett (til testverktøykassa)



---
class: inverse

# junit-quickcheck

- utvidelse til JUnit

- en annen måte .red[å tenke] på test-input og forventet resultat

- beskriver generelle .red[egenskaper]

- inspirert av tilsvarende verktøy for f.eks. Haskell og Scala


???

# junit-quickcheck
- utvidelse til JUnit
- litt annen måte å tenke:
  - test-input
  - forventede resultater
- beskriver generelle egenskaper
- tilsvarende verktøy, Haskell, Scala.


---

# Tradisjonell enhetstesting


- statiske eksempler

- corner-cases

???

tradisjonelle enhetstester: statiske eksempler -> fast resultat
god praksis å inkludere corner-cases.
jo flere eksempler man lager -> tryggere.


---

# .center[`reverse(String s)`]

???

Implementere metode som reverserer en String.

- hvilke tester?

--
count: false

- `reverse("abc") == "cba"`

--
count: false

- `reverse("xx") == "xx"`

--
count: false

- `reverse("x") == "x"`

--
count: false

- `reverse("") == ""`

--
count: false

- mer?

???

ca 60% testdekning av Java sin egen implementasjon av reverse-metoden på StringBuilder.

---

class: inverse
.smaller[
```java
public static String reverse(String s) {
    char[] chars = s.toCharArray();
    boolean hasSurrogates = false;
    int n = chars.length - 1;
    for (int j = (n-1) >> 1; j >= 0; j--) {
        int k = n - j;
        char cj = chars[j];
        char ck = chars[k];
        chars[j] = ck;
        chars[k] = cj;
        if (Character.isSurrogate(cj) || Character.isSurrogate(ck)) {
            hasSurrogates = true;
        }
    }
    if (hasSurrogates) {
        reverseAllValidSurrogatePairs(chars);
    }
    return new String(chars);
}

private static void reverseAllValidSurrogatePairs(char[] chars) {
    for (int i = 0; i < chars.length - 1; i++) {
        char c2 = chars[i];
        if (Character.isLowSurrogate(c2)) {
            char c1 = chars[i + 1];
            if (Character.isHighSurrogate(c1)) {
                chars[i++] = c1;
                chars[i] = c2;
            }
        }
    }
}
```
]

???

Ser slik ut. Jeg vet ikke hva alt her gjør for noe en gang. Hvordan skal jeg greie å teste at all denne koden faktisk gjør som den skal?



---
class: center


# `reverse(String s)`

???

Annen måte å beskrive reversering av strenger:


count: false

Reversering av
.red[sammensetningen av to vilkårlige stringer]


--
count: false

.bigger[
```java
reverse("abc" + "def")
```
]


???
count: false

er det samme som .red[reversering av hver streng] for seg,<br/>
satt sammen i .red[motsatt rekkefølge].

--

.vertical.large[=]

--
count: false

.bigger[```java
reverse("def") + reverse("abc")
```
]

---
class: center


# `reverse(String s)`

.bigger[
```java
reverse(s1 + s2)
```
]

.vertical.large[=]

.bigger[
```java
reverse(s2) + reverse(s1)
```
]

???

Stemmer for alle strenger.

---

# junit-quickcheck

```java



  @Theory
  public void reversering(@ForAll String s1, @ForAll String s2) {
    assertThat(reverse(s1 + s2), is(reverse(s2) + reverse(s1)));
  }


```

???

- Kan uttrykkes som en junit-quickcheck test.
- 100% testdekning av Java sin implementasjon.

- Test gyldig for alle strenger `s1` og `s2`.
- Kalles gjentatte ganger, tilfeldige verdier.
- Selve testmetoden markert med `@Theory`

---


# junit-quickcheck

```java
@RunWith(Theories.class)
public class ReverseStringTheory {

  @Theory
  public void reversering(@ForAll String s1, @ForAll String s2) {
    assertThat(reverse(s1 + s2), is(reverse(s2) + reverse(s1)));
  }

}
```

???

Instruerer JUnit til å benytte `Theories`-runner.
Kan ha `@Test`-annoterte metoder, kjøres på vanlig måte.

---


# junit-quickcheck: OCSP-lookup

???

Litt mer relevant eksempel.

*O*nline *C*ertificate *S*tatus *P*rotocol er protokollen for en tjeneste man kan spørre om **i utgangspunktet gyldige sertifikater har blitt revokert**, altså av ulike årsaker blitt **ansett som ugyldige**. F.eks. hvis sertifikatet har kommet på avveie, eller andre årsaker. OCSP er en **vanlig del av valideringsprosessen** av sertifikater. En OCSP-tjeneste er et vanlig **HTTP-endepunkt** man sender en **request** til og får en **respons** tilbake som **angir om sertifikatet er revokert eller ikke**.

--

```java
@Theory
public void ocspLookupIsUndecidedForAnythingButStatusCode200(
        @ForAll @InRange(min="100", max="599") int otherThan200) {

  assumeThat(otherThan200, not(200));


  given(ocspResponseStatus.getStatusCode()).willReturn(otherThan200);

  assertThat(certValidator.validate(certificate), is(UNDECIDED));

}
```

???

- alle andre HTTP respons-koder enn 200 skal resultere i "UNDECIDED"
- trenger ikke å velge en eller flere vilkårlige responskoder


---

# junit-quickcheck

- .red[Forretningsregler] &ndash; beregninger og algoritmer

- .red[Sikkerhet] &ndash; _allow/deny all except..._

- .red[Kategorisering] &ndash; sett av verdier som skal reduseres til et begrenset antall verdier

- "matematiske" definisjoner

???

På hvilke områder er junit-quickcheck relevant?

---

# junit-quickcheck

- et .red[tillegg] til vanlig enhetstesting

- vurder dersom faste eksempler føles .red[kunstige]

- .red[utfordrer] til å tenke litt annerledes

.center[&nbsp;]

.center[&nbsp;]

.center[.bigger[[github.com/pholser/junit-quickcheck](https://github.com/pholser/junit-quickcheck)]]


---
class: inverse, middle, center

## Takk for meg!

.red[&ndash;]

Rune Flobakk


<img src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDE5LjAuMCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8IURPQ1RZUEUgc3ZnIFBVQkxJQyAiLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4iICJodHRwOi8vd3d3LnczLm9yZy9HcmFwaGljcy9TVkcvMS4xL0RURC9zdmcxMS5kdGQiPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHZpZXdCb3g9IjAgMCAzOTguMyA5My4yIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAzOTguMyA5My4yIiB4bWw6c3BhY2U9InByZXNlcnZlIj4KCjxwYXRoIGZpbGw9IiNmZmZmZmYiIGQ9Ik0wLDBoMzQuOWMwLDAsMjguNy0yLjQsMjguNywyMS44YzAsMjQuMi0yNC44LDIyLjQtMjkuNSwyMi40bC0zNC4zLDBWMzMuNGgzNS42YzAsMCwxNS42LDAuNywxNS42LTExLjYKCWMwLTExLjktMTAuNi0xMS4xLTE2LTExLjFjLTUuNCwwLTM1LDAtMzUsMEwwLDB6Ii8+CjxwYXRoIGZpbGw9IiNmZmZmZmYiIGQ9Ik01OC45LDUyLjFDNjQuMiw1NS4zLDY5LDYxLDY5LDcwLjNjMCwyNC4yLTI0LjgsMjIuOC0yOS41LDIyLjhsLTM5LjYsMFY4Mi41bDQwLjcsMAoJYzAsMCwxNS44LDAuMiwxNS45LTEyLjFjMC4xLTguNS01LjMtMTQuOS0xMC41LTE4LjJINTguOXoiLz4KCjxwYXRoIGZpbGw9IiNmZmZmZmYiIGQ9Ik0xMDAsNTIuMWgxNC45YzAsNi43LDAsMTYuNCwwLDIxLjJjMCw2LjksNC4yLDkuMiw4LjMsOS4yYzQuMSwwLDM3LDAsNDAuNywwbDAuMSwxMC45Yy0zLjksMC00NCwwLTQ5LjEsMAoJYy01LjEsMC0xNC44LTMuMy0xNC44LTE3LjFDMTAwLDYwLjksMTAwLDUyLjEsMTAwLDUyLjF6Ii8+CjxwYXRoIGZpbGw9IiNmZmZmZmYiIGQ9Ik0xNjQsMzMuNGMtMy43LDAtMzgsMC00Mi4xLDBjLTQuMSwwLTctMC40LTctNS4xYzAtMC43LDAtMTEuNSwwLjEtMTIuM2MwLTUsMi42LTUuMyw2LjctNS4zCgljNC4xLDAsMzguNiwwLDQyLjMsMGwwLTEwLjdjLTMuOSwwLTQ4LDAtNDgsMFMxMDAtMS4xLDEwMCwxNS45YzAsNS45LDAsMTEuNywwLDExLjljMCwxNS4zLDExLjQsMTYuMywxNi41LDE2LjMKCWM1LjEsMCw0My41LDAsNDcuNCwwTDE2NCwzMy40eiIvPgo8cGF0aCBmaWxsPSIjZmZmZmZmIiBkPSJNMjcwLjYsMGMtMi4yLDIuNS0yMy4xLDI3LjQtMjUuNywzMGMtNC40LDQuMy0xMi4zLDMuNC0xOC43LDMuNGMtNi41LDAtNy42LTQuOS03LjYtOC4yCgljMC0zLjMsMC0yMi4xLDAtMjUuMWgtMTQuOGMwLDMuMiwwLjEsMjkuMiwwLjEsMzMuNGMwLDMuMywxLjUsMTAuNywxNC43LDEwLjdjMi41LDAsMjUuMiwwLDI1LjQsMGMzLjMsMCw4LjItMiwxMy45LTguNwoJYzEuNy0yLDI1LjgtMzEuMSwyOS44LTM1LjRIMjcwLjZ6Ii8+CjxwYXRoIGZpbGw9IiNmZmZmZmYiIGQ9Ik0yNTYsNTguOGMtMy4xLTMuNC01LjktNi43LTE1LjctNi43YzAsMC02LjYsMC0yMS43LDBjLTEwLjgsMC0xNC43LDcuOS0xNC43LDEyYzAsNC4xLDAsMjUuOSwwLDI5LjFsMTQuOCwwCgljMC0zLDAtMjEuNywwLTI1LjFjMC0zLjMsMS01LjIsNy41LTUuMmM2LjQsMCw1LjQsMCw2LjEsMGM2LjEsMCw5LjUsMC4yLDEyLjcsMy40YzIuNiwyLjYsMjQuMywyNC4zLDI2LjcsMjYuOWgxNwoJQzI4NC41LDg4LjcsMjU3LjgsNjAuNywyNTYsNTguOHoiLz4KPHBhdGggZmlsbD0iI2ZmZmZmZiIgZD0iTTM4MC4yLDBjLTIuMiwyLjUtMjMuMSwyNy40LTI1LjcsMzBjLTQuNCw0LjMtMTIuMywzLjQtMTguNywzLjRjLTYuNSwwLTcuNi00LjktNy42LTguMgoJYzAtMy4zLDAtMjIuMSwwLTI1LjFoLTE0LjhjMCwzLjIsMC4xLDI5LjIsMC4xLDMzLjRjMCwzLjMsMS41LDEwLjcsMTQuNywxMC43YzIuNSwwLDI1LjIsMCwyNS40LDBjMy4zLDAsOC4yLTIsMTMuOS04LjcKCWMxLjctMiwyNS44LTMxLjEsMjkuOC0zNS40SDM4MC4yeiIvPgo8cGF0aCBmaWxsPSIjZmZmZmZmIiBkPSJNMzY1LjYsNTguOGMtMy4xLTMuNC01LjktNi43LTE1LjctNi43YzAsMC02LjYsMC0yMS43LDBjLTEwLjgsMC0xNC43LDcuOS0xNC43LDEyYzAsNC4xLDAsMjUuOSwwLDI5LjEKCWwxNC44LDBjMC0zLDAtMjEuNywwLTI1LjFjMC0zLjMsMS01LjIsNy41LTUuMmM2LjQsMCw1LjQsMCw2LjEsMGM2LjEsMCw5LjUsMC4yLDEyLjcsMy40YzIuNiwyLjYsMjQuMywyNC4zLDI2LjcsMjYuOWgxNwoJQzM5NC4yLDg4LjcsMzY3LjQsNjAuNywzNjUuNiw1OC44eiIvPgo8L3N2Zz4K" alt="BEKK" style="width: 80px"/>

.footnote[
Twitter: [@rflob](http://twitter.com/rflob)

Laget med [Remark](https://github.com/gnab/remark)
]
