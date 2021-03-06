
package com.esotericsoftware.reflectasm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public abstract class FieldAccess {
	static public FieldAccess get (Class type) {
		AccessClassLoader loader = new AccessClassLoader(type.getClassLoader());

		ArrayList<Field> fields = new ArrayList();
		Class nextClass = type;
		while (nextClass != Object.class) {
			Field[] declaredFields = nextClass.getDeclaredFields();
			for (int i = 0, n = declaredFields.length; i < n; i++) {
				Field field = declaredFields[i];
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers)) continue;
				if (Modifier.isPrivate(modifiers)) continue;
				fields.add(field);
			}
			nextClass = nextClass.getSuperclass();
		}

		String className = type.getName();
		String accessClassName = className + "FieldAccess";
		if (accessClassName.startsWith("java.")) accessClassName = "reflectasm." + accessClassName;
		Class accessClass = null;
		try {
			accessClass = loader.loadClass(accessClassName);
		} catch (ClassNotFoundException ignored) {
		}
		if (accessClass == null) {
			String accessClassNameInternal = accessClassName.replace('.', '/');
			String classNameInternal = className.replace('.', '/');

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			MethodVisitor mv;
			cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, "com/esotericsoftware/reflectasm/FieldAccess",
				null);
			{
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, "com/esotericsoftware/reflectasm/FieldAccess", "<init>", "()V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
			{
				mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ILOAD, 2);

				if (!fields.isEmpty()) {
					Label[] labels = new Label[fields.size()];
					for (int i = 0, n = labels.length; i < n; i++)
						labels[i] = new Label();
					Label defaultLabel = new Label();
					mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

					for (int i = 0, n = labels.length; i < n; i++) {
						Field field = fields.get(i);
						Type fieldType = Type.getType(field.getType());

						mv.visitLabel(labels[i]);
						mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
						mv.visitVarInsn(ALOAD, 1);
						mv.visitTypeInsn(CHECKCAST, classNameInternal);
						mv.visitVarInsn(ALOAD, 3);

						switch (fieldType.getSort()) {
						case Type.BOOLEAN:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
							break;
						case Type.BYTE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
							break;
						case Type.CHAR:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
							break;
						case Type.SHORT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
							break;
						case Type.INT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
							break;
						case Type.FLOAT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
							break;
						case Type.LONG:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
							break;
						case Type.DOUBLE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
							break;
						case Type.ARRAY:
							mv.visitTypeInsn(CHECKCAST, fieldType.getDescriptor());
							break;
						case Type.OBJECT:
							mv.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
							break;
						}

						mv.visitFieldInsn(PUTFIELD, classNameInternal, field.getName(), fieldType.getDescriptor());
						mv.visitInsn(RETURN);
					}

					mv.visitLabel(defaultLabel);
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				}
				mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				mv.visitInsn(DUP);
				mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
				mv.visitInsn(DUP);
				mv.visitLdcInsn("Field not found: ");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
				mv.visitVarInsn(ILOAD, 2);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
				mv.visitInsn(ATHROW);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
			{
				mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
				mv.visitCode();
				mv.visitVarInsn(ILOAD, 2);

				if (!fields.isEmpty()) {
					Label[] labels = new Label[fields.size()];
					for (int i = 0, n = labels.length; i < n; i++)
						labels[i] = new Label();
					Label defaultLabel = new Label();
					mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

					for (int i = 0, n = labels.length; i < n; i++) {
						Field field = fields.get(i);

						mv.visitLabel(labels[i]);
						mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
						mv.visitVarInsn(ALOAD, 1);
						mv.visitTypeInsn(CHECKCAST, classNameInternal);
						mv.visitFieldInsn(GETFIELD, classNameInternal, field.getName(), Type.getDescriptor(field.getType()));

						Type fieldType = Type.getType(field.getType());
						switch (fieldType.getSort()) {
						case Type.BOOLEAN:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
							break;
						case Type.BYTE:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
							break;
						case Type.CHAR:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
							break;
						case Type.SHORT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
							break;
						case Type.INT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
							break;
						case Type.FLOAT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
							break;
						case Type.LONG:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
							break;
						case Type.DOUBLE:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
							break;
						}

						mv.visitInsn(ARETURN);
					}

					mv.visitLabel(defaultLabel);
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				}
				mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				mv.visitInsn(DUP);
				mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
				mv.visitInsn(DUP);
				mv.visitLdcInsn("Field not found: ");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
				mv.visitVarInsn(ILOAD, 2);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
				mv.visitInsn(ATHROW);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
			cw.visitEnd();
			byte[] data = cw.toByteArray();
			accessClass = loader.defineClass(accessClassName, data);
		}
		try {
			FieldAccess access = (FieldAccess)accessClass.newInstance();
			access.fields = fields.toArray(new Field[fields.size()]);
			return access;
		} catch (Exception ex) {
			throw new RuntimeException("Error constructing field access class: " + accessClassName, ex);
		}
	}

	private Field[] fields;

	abstract public void set (Object object, int fieldIndex, Object value);

	abstract public Object get (Object object, int fieldIndex);

	public int getIndex (String fieldName) {
		for (int i = 0, n = fields.length; i < n; i++) {
			Field field = fields[i];
			if (field.getName().equals(fieldName)) return i;
		}
		throw new IllegalArgumentException("Unable to find public field: " + fieldName);
	}

	public void set (Object object, String fieldName, Object value) {
		set(object, getIndex(fieldName), value);
	}

	public Object get (Object object, String fieldName) {
		return get(object, getIndex(fieldName));
	}

	public Field[] getFields () {
		return fields;
	}
}
