package baldi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VariableTest {
    private Variable<Integer> variable;

    @Before
    public void before() {
        List<Integer> domain = new LinkedList<>();
        domain.addAll(Arrays.asList(1, 2, 3, 4));
        Propagation propagation = new Propagation();
        variable = new Variable<>("name", domain, propagation);

    }

    @Test
    public void isInDomain() {
        assertTrue(variable.isInDomain(4));
        assertFalse(variable.isInDomain(5));
    }

    @Test
    public void removeValue() {
        assertTrue(variable.isInDomain(4));
        variable.removeValue(4);
        assertFalse(variable.isInDomain(4));
    }

    @Test
    public void getDomain() {
        Assert.assertEquals(variable.getDomain(), Arrays.asList(1, 2, 3, 4));
        variable.removeValue(4);
        Assert.assertEquals(variable.getDomain(), Arrays.asList(1, 2, 3));
    }

}