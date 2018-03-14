package custom.cache

import javax.sql.rowset.serial.SerialBlob
import java.sql.Blob

class CacheItemORM {

    CacheORM cache
    String key
    Blob value

    // TODO: Add expiration time support. It can be configured in the cache definition, from app config (application.yml)

    static constraints = {
        cache nullable: false
        key nullable: false, unique: 'cache'
        value nullable: true
    }

    static mapping = {
        datasource 'cache'
        table 'cache_item'
        value type: 'blob'
    }




    void setValue(Serializable value) {
        this.value = value != null ? new SerialBlob(serialToByteArray(value)) : null
    }

    Serializable getValue() {
        return this.value == null ? null : byteArrayToSerial(this.value.getBytes(0l, (int) this.value.length()))
    }


    private static byte[] serialToByteArray(Serializable obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ObjectOutput out = null
        byte[] byteArray = null
        try {
            out = new ObjectOutputStream(bos)
            out.writeObject(obj)
            out.flush()
            byteArray = bos.toByteArray()
        } finally {
            try {
                bos.close()
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        byteArray
    }

    private static Serializable byteArrayToSerial(byte[] bytes) {
        ObjectInput oi = null
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes)
        Serializable serial = null
        try {
            oi = new ObjectInputStream(bis)
            Object o = oi.readObject()
            serial = (Serializable) o
        } finally {
            try {
                if (oi != null) {
                    oi.close()
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        serial
    }
}
