package no.rflob.jqchk;

import static no.rflob.jqchk.Strings.reverse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ReverseStringTest {

    @Test
    public void reverseStringExamples() {
        assertThat(reverse("abc"), is("cba"));
        assertThat(reverse("xx"), is("xx"));
        assertThat(reverse(""), is(""));
    }
}
