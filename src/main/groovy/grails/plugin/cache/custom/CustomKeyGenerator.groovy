package grails.plugin.cache.custom

import grails.plugin.cache.GrailsCacheKeyGenerator
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Slf4j
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.cache.interceptor.SimpleKeyGenerator

import java.lang.reflect.Method

@CompileStatic
@Slf4j
class CustomKeyGenerator implements KeyGenerator, GrailsCacheKeyGenerator {

    private final KeyGenerator innerKeyGenerator

    CustomKeyGenerator(KeyGenerator innerKeyGenerator) {
        log.debug "[m:CustomKeyGenerator] innerKeyGenerator: {}", innerKeyGenerator
        this.innerKeyGenerator = innerKeyGenerator
    }

    CustomKeyGenerator() {
        this.innerKeyGenerator = new SimpleKeyGenerator()
        log.debug "[m:CustomKeyGenerator] innerKeyGenerator: {}", innerKeyGenerator
    }



    // ------------------ //
    // BEGIN KeyGenerator //
    // ------------------ //

    Object generate(Object target, Method method, Object... params) {
        Class<?> objClass = AopProxyUtils.ultimateTargetClass(target)

        log.debug "[m:generate] params: {}", params

        return new CacheKey(
                objClass.getName().intern(),
                method.toString().intern(),
                target.hashCode(), innerKeyGenerator.generate(target, method, params))
    }


    // ---------------- //
    // END KeyGenerator //
    // ---------------- //


    // ----------------------------------- //
    // -- BEGIN GrailsCacheKeyGenerator -- //
    // ----------------------------------- //

    @Override
    Serializable generate(String className, String methodName, int objHashCode, Closure keyGenerator) {
        final Object simpleKey = keyGenerator.call()
        return new TemporaryGrailsCacheKey(className, methodName, objHashCode, simpleKey)
    }

    @Override
    Serializable generate(String className, String methodName, int objHashCode, Map methodParams) {
        final Object simpleKey = methodParams
        return new TemporaryGrailsCacheKey(className, methodName, objHashCode, simpleKey)
    }

    // --------------------------------- //
    // -- END GrailsCacheKeyGenerator -- //
    // --------------------------------- //


    // ------------- //
    // -- Private -- //
    // ------------- //

    @SuppressWarnings("serial")
    @EqualsAndHashCode
    private static final class CacheKey implements Serializable {
        final String targetClassName
        final String targetMethodName
//        final int targetObjectHashCode
        final Object simpleKey

        CacheKey(String targetClassName, String targetMethodName,
                 int targetObjectHashCode, Object simpleKey) {

            log.debug "[m:CacheKey] simpleKey: {}", simpleKey

            this.targetClassName = targetClassName
            this.targetMethodName = targetMethodName
//            this.targetObjectHashCode = targetObjectHashCode
            this.simpleKey = simpleKey
        }
    }


    @EqualsAndHashCode
    @CompileStatic
    private static class TemporaryGrailsCacheKey implements Serializable {
        final String targetClassName
        final String targetMethodName
//        final int targetObjectHashCode
        final Object simpleKey

        TemporaryGrailsCacheKey(String targetClassName, String targetMethodName,
                                int targetObjectHashCode, Object simpleKey) {
            // this.targetClassName = targetClassName
            // this.targetMethodName = targetMethodName
            // this.targetObjectHashCode = targetObjectHashCode

            /**
             * Es posa només la simpleKey i serà només amb aquest paràmetre el quin farà un únic hash
             * Mirar @EqualsAndHashCode
             */
            this.simpleKey = simpleKey

            log.debug "[m:TemporaryGrailsCacheKey] this: {}, simpleKey: {}", this, simpleKey
        }
    }


}