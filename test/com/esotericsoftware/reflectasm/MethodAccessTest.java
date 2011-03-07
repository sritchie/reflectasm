
package com.esotericsoftware.reflectasm;

import com.esotericsoftware.reflectasm.FieldAccessTest.EmptyClass;

import junit.framework.TestCase;

public class MethodAccessTest extends TestCase {
	public void testInvoke () {
		MethodAccess access = MethodAccess.get(SomeClass.class);
		SomeClass someObject = new SomeClass();
		Object value;

		value = access.invoke(someObject, "getName");
		assertEquals(null, value);
		value = access.invoke(someObject, "setName", "sweet");
		assertEquals(null, value);
		value = access.invoke(someObject, "getName");
		assertEquals("sweet", value);
		value = access.invoke(someObject, "setName", (Object)null);
		assertEquals(null, value);
		value = access.invoke(someObject, "getName");
		assertEquals(null, value);

		value = access.invoke(someObject, "getIntValue");
		assertEquals(0, value);
		value = access.invoke(someObject, "setIntValue", 1234);
		assertEquals(null, value);
		value = access.invoke(someObject, "getIntValue");
		assertEquals(1234, value);

		value = access.invoke(someObject, "methodWithManyArguments", 1, 2f, 3, 4.2f, null, null, null);
		assertEquals("test", value);

		int index = access.getIndex("methodWithManyArguments", int.class, float.class, Integer.class, Float.class, SomeClass.class,
			SomeClass.class, SomeClass.class);
		assertEquals(access.getIndex("methodWithManyArguments"), index);
	}

	public void testEmptyClass () {
		MethodAccess access = MethodAccess.get(EmptyClass.class);
		try {
			access.getIndex("name");
			fail();
		} catch (IllegalArgumentException expected) {
			expected.printStackTrace();
		}
		try {
			access.getIndex("name", String.class);
			fail();
		} catch (IllegalArgumentException expected) {
			expected.printStackTrace();
		}
		try {
			access.invoke(new EmptyClass(), "meow", "moo");
			fail();
		} catch (IllegalArgumentException expected) {
			expected.printStackTrace();
		}
		try {
			access.invoke(new EmptyClass(), 0);
			fail();
		} catch (IllegalArgumentException expected) {
			expected.printStackTrace();
		}
		try {
			access.invoke(new EmptyClass(), 0, "moo");
			fail();
		} catch (IllegalArgumentException expected) {
			expected.printStackTrace();
		}
	}

	static public class EmptyClass {
	}

	static public class SomeClass {
		private String name;
		private int intValue;

		public String getName () {
			return name;
		}

		public void setName (String name) {
			this.name = name;
		}

		public int getIntValue () {
			return intValue;
		}

		public void setIntValue (int intValue) {
			this.intValue = intValue;
		}

		public String methodWithManyArguments (int i, float f, Integer I, Float F, SomeClass c, SomeClass c1, SomeClass c2) {
			return "test";
		}
	}
}
