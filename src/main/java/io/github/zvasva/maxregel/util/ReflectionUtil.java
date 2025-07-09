package io.github.zvasva.maxregel.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.zvasva.maxregel.util.Coerce.*;

/**
 * Utility class providing various methods for working with reflection to inspect and manipulate objects.
 *
 * @author Arvid Halma
 */
public class ReflectionUtil {

    public enum ValueSource {GETTER, METHOD, FIELD, ANY}

    private static final Map<Class<?>, Map<String, Field>> DECLARED_FIELD_CACHE = new ConcurrentHashMap<>();

    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        return DECLARED_FIELD_CACHE.computeIfAbsent(clazz, c -> {
            Map<String, Field> fields = new ConcurrentHashMap<>();
            for (Field field : c.getDeclaredFields()) {
                field.setAccessible(true); // once
                fields.put(field.getName(), field);
            }
            return fields;
        }).get(fieldName);
    }

    public static Object getValue(final Object obj, final String fieldName, ValueSource source) {
        return switch (source) {
            case GETTER -> getValueFromGetter(obj, fieldName);
            case METHOD -> getValueFromMethod(obj, fieldName);
            case FIELD -> getValueFromField(obj, fieldName);
            case ANY -> getValue(obj, fieldName);
        };
    }

    /**
     * Retrieves the value of a specified field from a given object. The method first attempts to
     * use a getter method for the field, if present. If no getter method is found, it directly
     * accesses the field, even if it is private.
     *
     * @param obj       The object from which the field value is to be extracted.
     * @param fieldName The name of the field whose value is to be retrieved.
     * @return The value of the specified field, or null if the field does not exist or cannot be accessed.
     */
    public static Object getValue(Object obj, String fieldName) {
        if( fieldName == null || fieldName.isBlank())
            return null;


        try {
            // Check if the getter method exists
            // Capitalize the first letter of the field to create the getter method name.
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getterMethod = obj.getClass().getMethod(getterName);
            return getterMethod.invoke(obj);  // Use the getter to get the value
        } catch (Exception e) {
            try {
                // Verbatim name (e.g. "name()" without "get"
                Method getterMethod = obj.getClass().getMethod(fieldName);
                return getterMethod.invoke(obj);
            } catch (Exception e2) {
                try {
                    // No getter found, proceed to access the field directly
                    Field field = obj.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);  // Allows access to private fields
                    return field.get(obj);  // Retrieve the value from the field directly
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }

    public static Object getValueFromGetter(Object obj, String fieldName) {
        try {
            // Check if the getter method exists
            // Capitalize the first letter of the field to create the getter method name.
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getterMethod = obj.getClass().getMethod(getterName);
            return getterMethod.invoke(obj);  // Use the getter to get the value
        } catch (Exception ignored) {}
        return null;
    }

    public static Object getValueFromMethod(Object obj, String fieldName) {
        try {
            // Verbatim name (e.g. "name()" without "get"
            Method getterMethod = obj.getClass().getMethod(fieldName);
            return getterMethod.invoke(obj);
        } catch (Exception ignored) {}
        return null;
    }

    public static Object getValueFromField(Object obj, String fieldName) {
        try {
            // No getter found, proceed to access the field directly
            Field field = getDeclaredField(obj.getClass(), fieldName);
            return field.get(obj);  // Retrieve the value from the field directly
        } catch (Exception ignored) {}
        return null;
    }
    /**
     * Checks if a specified field has a corresponding getter method or is declared
     * in the given object's class. The field can be a private or public field.
     *
     * @param obj The object whose class is inspected for the specified field.
     * @param fieldName The name of the field to check for existence.
     * @return true if the field or its getter method exists, false otherwise.
     */
    public static boolean hasField(Object obj, String fieldName, ValueSource source) {
        return switch (source) {
            case GETTER -> hasGetterForField(obj, fieldName);
            case METHOD -> hasMethodForField(obj, fieldName);
            case FIELD -> hasActualField(obj, fieldName);
            case ANY -> hasField(obj, fieldName);
        };
    }

    /**
     * Checks if a specified field has a corresponding getter method or is declared
     * in the given object's class. The field can be a private or public field.
     *
     * @param obj The object whose class is inspected for the specified field.
     * @param fieldName The name of the field to check for existence.
     * @return true if the field or its getter method exists, false otherwise.
     */
    public static boolean hasField(Object obj, String fieldName) {
        if( fieldName == null || fieldName.isBlank())
            return false;

        try {
            // Check if the getter method exists
            // Capitalize the first letter of the field to create the getter method name.
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getterMethod = obj.getClass().getMethod(getterName);
            return true;  // Use the getter to get the value
        } catch (Exception e) {
            try {
                // Verbatim name (e.g. "name()" without "get"
                Method getterMethod = obj.getClass().getMethod(fieldName);
                return true;  // Use the getter to get the value
            } catch (Exception e2) {
                try {
                    // No getter found, proceed to access the field directly
                    Field field = obj.getClass().getDeclaredField(fieldName);
                    return true;  // Retrieve the value from the field directly
                } catch (Exception e3) {
                    return false;
                }
            }
        }
    }

    public static boolean hasGetterForField(Object obj, String fieldName) {
        try {
            // Check if the getter method exists
            // Capitalize the first letter of the field to create the getter method name.
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            obj.getClass().getMethod(getterName);
            return true;  // Use the getter to get the value
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean hasMethodForField(Object obj, String fieldName) {
        try {
            // Verbatim name (e.g. "name()" without "get"
            obj.getClass().getMethod(fieldName);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean hasActualField(Object obj, String fieldName) {
        try {
            // No getter found, proceed to access the field directly
            Field field = obj.getClass().getDeclaredField(fieldName);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Retrieves the names of all getter methods from the given object's class.
     * A method is considered a getter if its name starts with "get", it has no parameters,
     * and it returns a value (i.e., it does not return void).
     *
     * @param obj The object whose class is inspected for getter methods.
     * @return A list of names of the getter methods, with the "get" prefix removed and the first letter converted to lowercase.
     */
    public static List<String> allGetterNames(Object obj) {
        List<String> getterValues = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        // Iterate through all declared methods
        for (Method method : clazz.getDeclaredMethods()) {
            // Check if the method is a getter: starts with "get", has no parameters, and returns a value
            String name = method.getName();
            if (name.startsWith("get")
                    && method.getParameterCount() == 0
                    && !void.class.equals(method.getReturnType())) {

                name = name.substring(3);
                if( name.isEmpty())
                    continue;
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                getterValues.add(name);
            }
        }

        return getterValues;
    }


    /**
     * Retrieves the field names from the given object's class.
     *
     * @param obj The object whose class is inspected for getter methods.
     * @return A list of names of the getter methods, with the "get" prefix removed and the first letter converted to lowercase.
     */
    public static List<String> allFieldNames(Object obj) {
        Class<?> clazz = obj.getClass();
        return Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).toList();
    }

    /**
     * Retrieves the names of all getter methods from the given object's class.
     * A method is considered a getter if its name starts with "get", it has no parameters,
     * and it returns a value (i.e., it does not return void).
     *
     * @param obj The object whose class is inspected for getter methods.
     * @return A list of names of the getter methods, with the "get" prefix removed and the first letter converted to lowercase.
     */
    public static List<String> allGetLikeNames(Object obj) {
        List<String> result = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        // Iterate through all declared methods
        for (Method method : clazz.getDeclaredMethods()) {
            // Check if the method is a getter: starts with "get", has no parameters, and returns a value
            String name = method.getName();
            if("hashCode".equals(name) || "toString".equals(name)){
                continue;
            }

            if (method.getParameterCount() == 0 && !void.class.equals(method.getReturnType())) {
                if(name.startsWith("get")) {
                    name = name.substring(3);
                    if (name.isEmpty())
                        continue;
                    name = name.substring(0, 1).toLowerCase() + name.substring(1);
                    result.add(name);
                } else {
                    result.add(name);
                }
            }
        }

        return result;
    }

    /**
     * Converts an object's getter methods to a Map, where the keys are the property names
     * (derived from the getter method names) and the values are the corresponding property values.
     *
     * @param obj The object from which property names and values are to be extracted.
     * @return A Map containing property names and values derived from the object's getter methods.
     */
    public static Map<String, Object> asMap(Object obj) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String getter : allGetLikeNames(obj)) {
            result.put(getter, getValue(obj, getter));
        }
        return result;
    }

    /**
     * Overwrite target fields with source fields. Later sources will overwrite earlier ones (if not null).
     *
     * @param target the object to update
     * @param sources an objects whose values are used in order
     * @param <T> the target type
     * @return the target with updated fields
     */
    public static <T> T assignFields(T target, List<?> sources) {
        for (Object source : sources) {
            if(source instanceof Map m) {
                assignFields(target, m, false);
            } else {
                assignFields(target, source, false);
            }
        }
        return target;
    }

    /**
     * Overwrite target fields with source fields, if the source field is not null.
     *
     * @param target the object to update
     * @param source an object whose values are used
     * @param <T> the target type
     * @return the target with updated fields
     */
    public static <T> T assignFields(T target, Object source) {
        return assignFields(target, source, false);
    }

    /**
     * Overwrite target fields with source fields.
     *
     * @param target the object to update
     * @param source an object whose values are used
     * @param overwriteWithNull overwrite the target field, even if the source field is null
     * @param <T> the target type
     * @return the target with updated fields
     */
    public static <T> T assignFields(T target, Object source, boolean overwriteWithNull) {
        final Field[] sourceFields = source.getClass().getDeclaredFields();
        for (Field sourceField : sourceFields) {
            try {
                sourceField.setAccessible(true);
                final Object sourceVal = sourceField.get(source);
                assignField(target, sourceField.getName(), sourceVal, overwriteWithNull);
            } catch (Exception ignored) {}
        }
        return target;
    }

    /**
     * Write given map values to the fields of the target object if the value is not null.
     *
     * @param target the object to update
     * @param source a map with field names as key, the values as Object
     * @param <T>    the type of object
     * @return the updated object
     */
    public static <T> T assignFieldsFromMap(T target, Map<String, ?> source) {
        return assignFieldsFromMap(target, source, false);
    }

    /**
     * Write given map values to the fields of the target object.
     *
     * @param target the object to update
     * @param source a map with field names as key, the values as Object
     * @param overwriteWithNull overwrite the target field, even if the source field is null
     * @param <T>    the type of object
     * @return the updated object
     */
    public static <T> T assignFieldsFromMap(T target, Map<String, ?> source, boolean overwriteWithNull) {
        for (Map.Entry<String, ?> sourceEntry : source.entrySet()) {
            assignField(target, sourceEntry.getKey(), sourceEntry.getValue(), overwriteWithNull);
        }
        return target;
    }

    /**
     * Write given map values to the fields of the target object.
     *
     * @param target the object to update
     * @param source a map with field names as key, the values as Object
     * @param overwriteWithNull overwrite the target field, even if the source field is null
     * @param <T>    the type of object
     * @return the updated object
     */
    public static <T> T assignFieldsFromMap(T target, Map<String, ?> source, boolean overwriteWithNull, ValueSource valueSource) {
        for (Map.Entry<String, ?> sourceEntry : source.entrySet()) {
            assignField(target, sourceEntry.getKey(), sourceEntry.getValue(), overwriteWithNull);
        }
        return target;
    }


    /**
     * Write given value to the target's field if it exists and the value is not null.
     *
     * @param <T>               the type of object
     * @param target            the object to update
     * @param fieldName         key
     * @param value             new value
     * @return the updated object
     */
    public static <T> T assignField(T target, String fieldName, Object value) {
        return assignField(target, fieldName, value, false);
    }

    /**
     * Write given value to the target's field if it exists.
     *
     * @param <T>               the type of object
     * @param target            the object to update
     * @param fieldName         key
     * @param value             new value
     * @param overwriteWithNull overwrite the target field, even if the source field is null
     * @return the updated object
     */
    public static <T> T assignField(T target, String fieldName, Object value, boolean overwriteWithNull) {
        if(!overwriteWithNull && value == null){
            return target;
        }
        try {
            Class<?> targetClass = target.getClass();
            final Field f = getDeclaredField(targetClass, fieldName);
            f.setAccessible(true);
            final Class<?> ft = f.getType();
            if (value == null || ft.equals(value.getClass())) {
                f.set(target, value);
                return target;
            }

            // attempt coercing the val to the assignment, e.g. int x = "10" // string to int
            if (ft.equals(String.class)) {
                f.set(target, value.toString());
            } else if (Character.class.equals(ft)) {
                f.set(target, asCharacter(value));
            } else if (ft == Character.TYPE) {
                f.setChar(target, asCharPrimitive(value));
            } else if (Boolean.class.equals(ft)) {
                f.set(target, asBoolean(value));
            } else if (ft == Boolean.TYPE) {
                f.setBoolean(target, asBoolean(value));
            } else if (Integer.class.equals(ft)) {
                f.set(target, asInteger(value));
            } else if (ft == Integer.TYPE) {
                f.setInt(target, asIntPrimitive(value));
            } else if (Long.class.equals(ft)) {
                f.set(target, asLong(value));
            } else if (ft == Long.TYPE) {
                f.setLong(target, asLongPrimitive(value));
            } else if (Short.class.equals(ft)) {
                f.set(target, asShort(value));
            } else if (ft == Short.TYPE) {
                f.setShort(target, asShortPrimitive(value));
            } else if (Byte.class.equals(ft)) {
                f.set(target, asByte(value));
            } else if (ft == Byte.TYPE) {
                f.setByte(target, asBytePrimitive(value));
            } else if (Double.class.equals(ft)) {
                f.set(target, asDouble(value));
            } else if (ft == Double.TYPE) {
                f.setDouble(target, asDoublePrimitive(value));
            } else if (Float.class.equals(ft)) {
                f.set(target, asFloat(value));
            } else if (ft == Float.TYPE) {
                f.setFloat(target, asFloatPrimitive(value));
            } else {
                f.set(target, value);
            }

        } catch (Exception ignored) {
            // parsing errors or additional values for which no fields exist in the target
            // System.err.println(ignored);
        }

        return target;
    }


}