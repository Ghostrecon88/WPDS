package test.cases.string;

import org.junit.Test;

import test.cases.fields.Alloc;
import test.core.AbstractBoomerangTest;

public class StringTest extends AbstractBoomerangTest {
	@Test
	public void stringConcat() {
		Object query = "a" + "b";
		if (staticallyUnknown())
			query += "c";
		System.out.println(query);
		queryFor(query);
	}

	@Test
	public void stringToCharArray() {
		char[] s = "password".toCharArray();
		queryFor(s);
	}

	@Test
	public void stringBuilderTest() {
		StringBuilder b = new StringBuilder("Test");
		b.append("ABC");
		String s = b.toString();
		queryFor(s);
	}

	@Test
	public void stringBuilder1Test() {
		String alloc = "Test";
		MyStringBuilder b = new MyStringBuilder(alloc);
		String s = b.toString();
		queryFor(s);
	}

	private static class MyAbstractStringBuilder {
		/**
		 * The value is used for character storage.
		 */
		char[] value;

		/**
		 * The count is the number of characters used.
		 */
		int count;

		/**
		 * This no-arg constructor is necessary for serialization of subclasses.
		 */
		MyAbstractStringBuilder() {
		}

		/**
		 * Creates an AbstractStringBuilder of the specified capacity.
		 */
		MyAbstractStringBuilder(int capacity) {
			value = new char[capacity];
		}
	    public MyAbstractStringBuilder append(String str) {
			// if (str == null)
			// return appendNull();
	        int len = str.length();
//	        ensureCapacityInternal(count + len);
	        str.getChars(0, len, value, count);
	        count += len;
	        return this;
	    }
	}
	private static class MyStringBuilder extends MyAbstractStringBuilder{
	    public MyStringBuilder(String str) {
	        super(str.length() + 16);
	        append(str);
	    }
	    @Override
	    public String toString() {
	        // Create a copy, don't share the array
	        return new String(value, 0, count);
	    }
	}
}
