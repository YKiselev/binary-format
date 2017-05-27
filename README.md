[![Maven Central](https://img.shields.io/badge/binary--format-1.33-brightgreen.svg)](https://search.maven.org/#artifactdetails%7Ccom.github.ykiselev%7Cbinary-format%7C1.33%7Cjar)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Synopsis

This is a small library defining simple binary format for Java class serialization/deserialization. Out of the box primitive types (and arrays of primitive types) are supported, custom user class support can be added by plugging in serialization providers. To tailor different needs one can use custom user-type-to-de-serializer mapping modes - for example, storing some _userTypeId_ or _className_ or even without any type information at all (this may be useful in cases when type of serialized instance always matches corresponding field type). 

## Code Example

Setting up serializer (well, for only one class here, but you got the idea)
```java
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    WritableMedia media = new SimpleWritableMedia(
            new OutputStreamBinaryOutput(bos),
            new UserTypeOutput() {
                @Override
                public <T> void write(WritableMedia media, T value) throws IOException {
                    if (value instanceof Item) {
                        ((Item) value).write(media);
                    } else {
                        throw new UnsupportedOperationException("Unsupported value: " + value);
                    }
                }
            }
    );
    
    media.writeObject(new Item(1, "Some item with id and decsription"));

    // here we are
    byte[] data = bos.toByteArray()

```
And method Item#write may look like this
```java
public final class Item {

    private final int id;

    private final String description;

    public Item(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public void write(WritableMedia writableMedia) throws IOException {
        writableMedia.writeInt(this.id);
        writableMedia.writeString(this.description);
    }
}
```
Here we just put class fields one-by-one to the media, because in next example we know the target type of object to de-serialize. In real life we often need to put some type id or even class name to media to be able to de-serialize it later. But this is up to you to decide what strategy fits best, same as decision about how to actually serialize object - by providing some method in target class itself (Item#write) of by some external helper class (perhaps ExternalItemWriter#write). 

Binary array, returned by `bos.toByteArray()` in example above now looks like this:
```java
byte[]{
        Types.USER_TYPE,
        Types.BYTE, 1,
        Types.STRING, 33,'S','o','m','e',' ','i','t','e','m',' ','w','i','t','h',' ','i','d',' ','a','n','d',' ','d','e','c','s','r','i','p','t','i','o','n',
        Types.END_MARKER
}
```
Where  
`Types.USER_TYPE` - is a type marker, each field or type has one (see org.uze.binary.format.Types for details). In our case it tells us that there is a user type.  
`Types.BYTE, 1` - is a first field `id` serialized with value 1. Field has type int but value if small enough to fit to one byte so it was saved as byte. Library always tries to occupy as much as possible room for _single_ values. On a contrary array elements are always saved as-is (4 bytes for int, 2 bytes for short, etc).  
`Types.STRING, 33,'S','o','m','e',...` - is a second field `description` serialized as UTF-8 with length of 33 **bytes**. Please note - for strings length is stored in bytes, not in characters!  
`Types.END_MARKER` - is a user type end marker, this is added by library automatically upon completion of a call to `UserTypeOutput#write`.  
One important note here is that if we try to put null value - `media.writeObject(null)`, it will be handled by library without a call to `UserTypeOutput#write`. In this case an array of bytes would look like this:
```java
byte[]{
        Types.NULL
}
```
In general each value is stored as a pair (type,data) where type is single byte and data is a type-dependent array of bytes.  
Lengths of arrays and strings are stored using variable length sequence of bytes, where only 7 lower bits of each byte is used, so if value fit 7 bits it will occupy one byte, if it fits 14 bits it will be two bytes and so on. This is because higher bit (8) when set is used as a marker "more data in next byte". For details see `org.uze.binary.format.media.SimpleWritableMedia.writeLength`.

Now de-serialization (again for simplicity only one user type supported)
```java
        ReadableMedia media = return new SimpleReadableMedia(
                new InputStreamBinaryInput(
                        new ByteArrayInputStream(data)
                ),
                new UserTypeInput() {
                    @Override
                    public <T> T read(@NotNull ReadableMedia media, Class<T> clazz) throws IOException {
                        if (Item.class.equals(clazz)) {
                            return clazz.cast(new Item(media.readInt(), media.readString()));
                        }
                        throw new UnsupportedOperationException("Unknown class:" + clazz);
                    }
                },
                new SimpleArrayFactory(16)
        );
        
        Item result = media.getObject(Item.class);
```

Of course in real life implementors of `UserTypeOutput` and `UserTypeInput` will be much more complex, perhaps including some registries of user-type serializers/de-serializers.

## Motivation

Because I like it.

## Installation

For maven projects add dependency to pom.xml
```xml
<dependency>
    <groupId>com.github.ykiselev</groupId>
    <artifactId>binary-format</artifactId>
    <version>1.32</version>
</dependency>
```

## API Reference

See the [Javadoc][javadoc] catalog for complete API reference.

## Tests

`mvn clean test` 
or run specific test: 
* com.github.ykiselev.binary.format.SimpleWritableMediaTest
* com.github.ykiselev.binary.format.SimpleReadableMediaTest


## Contributors

Please e-mail me if you need more info or want to improve something: uze@yandex.ru

## Downloads

Download [the latest jar][dl] or [snapshot][snap]

## License

This library is licensed under the Apache License, Version 2.0.

[dl]: https://search.maven.org/remote_content?g=com.github.ykiselev&a=binary-format&v=LATEST
[snap]: https://oss.sonatype.org/content/repositories/snapshots/com/github/ykiselev/binary-format/
[javadoc]: https://ykiselev.github.io/binary-format/1.x/
