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

- ❤ enhetstester


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

# `reverse(String s)`

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

???

Ser slik ut. Jeg vet ikke en gang hva alt her gjør for noe en gang. Hvordan skal vi greie å teste at all denne koden faktisk gjør som den skal?



---
class: center


# `reverse(String s)`

???

Annen måte å beskrive reversering av strenger:

--
count: false

Reversering av
.red[sammensetningen av to vilkårlige stringer]

--
count: false

```java
reverse("abc" + "def")
```


--
count: false

er det samme som .red[reversering av hver streng] for seg,<br/>
satt sammen i .red[motsatt rekkefølge].

--
count: false

```java
reverse("def") + reverse("abc")
```


---
class: center


# `reverse(String s)`

Reversering av
.red[sammensetningen av to vilkårlige stringer]

```java
reverse(s1 + s2)
```

er det samme som .red[reversering av hver streng] for seg,<br/>
satt sammen i .red[motsatt rekkefølge].


```java
reverse(s2) + reverse(s1)
```

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

alle andre HTTP respons-koder enn 200 skal resultere i "UNDECIDED"


---

# junit-quickcheck

- .red[Forretningsregler] &ndash; beregninger og algoritmer
- .red[Sikkerhet] &ndash; _allow/deny all except..._
- .red[Redusering] &ndash; sett av verdier som skal gi ett bestemt resultat
- "matematiske" definisjoner

???

På hvilke områder er junit-quickcheck relevant?

---

# junit-quickcheck

- et .red[tillegg] til vanlig enhetstesting
- vurder dersom faste eksempler føles .red[kunstige]
- .red[utfordrer] til å tenke litt annerledes


---
class: inverse, middle, center

## Takk for meg!

Rune Flobakk

BEKK

.footnote[Laget med [Remark](https://github.com/gnab/remark)]
