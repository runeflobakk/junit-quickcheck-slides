package no.rflob.jqchk;

import static no.rflob.jqchk.Strings.reverse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.contrib.theories.Theory;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.ForAll;

@RunWith(org.junit.contrib.theories.Theories.class)
public class ReverseStringTheory {

    @Theory
    public void reversering(@ForAll String s1, @ForAll String s2) {

        assertThat(reverse(s1 + s2), is(reverse(s2) + reverse(s1)));

    }

}
