package custom.cache

import org.springframework.beans.factory.annotation.Value


class CacheORM {

    String name
    Date dateCreated
    Date lastUpdated

    // TODO: Add TTL to all items in this cache from app config

    static constraints = {
        name nullable: false, blank: false
    }

    static mapping = {
        datasource 'cache'
        table 'cache'
    }


    @Override
    public String toString() {
        return "CacheORM{" +
                "id=" + id +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
