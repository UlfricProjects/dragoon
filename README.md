# Dragoon
Context &amp; Dependency Injection

# ObjectFactory
The core of dragoon - almost all functionality of dragoon is a result of an execution within ObjectFactory.

## Goal of ObjectFactory
1. To handle initialization of objects
2. To provide said objects to requesting processes
3. To provide additional functionality on said objects, eg. dependency injection

## Usage of ObjectFactory

### Obtaining an instance of ObjectFactory
An instance of ObjectFactory can be provided with `ObjectFactory.newInstance()`, however this should very rarely be used.
It can also be provided by injecting it into a field of a class, creating a child factory of the factory used to request the class. Child factories can (and will) access parent factories when performing lookups, however a parent factory has no knowledge of the child factory.

### Using ObjectFactory to obtain objects
To create an instance of a class, you may use 
`T instance = (T) factory.request(T.class); // Request returns Object`
or the shorthand of the same statement: `T instance = factory.requestExact(T.class);`

## Bindings
A certain type may be "bound" to another through ObjectFactory bindings. Binding A.class to B.class makes a request of A.class return an instance of B, as A.class is now considered B.class. This may cause unexpected type returns, so ensure when using requestExact you are sure that what you are requesting is really an instance of your variable type.

Bindings can be created with `factory.bind(A.class).to(B.class);`. This is useful for example to bind an interface to its implementation: i.e. `factory.bind(SomeType.class).to(SomeTypeImpl.class);` where SomeType is an interface and SomeTypeImpl is a class that implements SomeType. You can then use `SomeType instance = factory.requestExact(SomeType.class);`, which will return an instance of SomeTypeImpl.class.

## Scopes
Scopes define how to initialize a class. This is achieved with a Scope annotation (eg. @interface Default) and a strategy paired alongside it (eg. class DefaultScopeStrategy implements ScopeStrategy). Two methods must be overridden - getOrEmpty(Class<T>) and getOrCreate(Class<T>), which both return a Scoped<T>. In getOrCreate, a scope will lookup whether it already has defined initialization procedures of a type - if it has, it will return an initialized T wrapped in a Scope object, if not it will create an initialization procedure and return an initialized T wrapped in a Scope object. getOrEmpty achieves the same goal, but if the initialization procedure of the type is undefined, it will simply return an empty Scope object.

Scopes are useful to provide different types of initialization. The default scope strategy (DefaultScopeStrategy) simply attempts to invoke a no-arg constructor and return the constructed instance. Another example is the SharedScopeStrategy, which will only create maximum one instance of each type (effectively a singleton in the context of the factory). Scopes of types are defined through including the scope's annotation on the type (eg. @Shared above the class declaration). @Default is inferred if no scope is defined.

Scopes can be registered as a binding, through `factory.bind(XYZScopeAnnotation.class).to(XYZScopeStrategy.class);`.

## Dependency Injection
When an ObjectFactory creates an instance, it will find all fields annotated with @Inject and attempt to provide them with an instance of the field type, in the same way one would use ObjectFactory#request(Class). Placing @Inject on an ObjectFactory field will set the field to a child factory of the factory used to request the class.
Note: As the factory must construct the object to access its fields, @Inject'ed fields should not be accessed by the constructor as they will be null. To safely access the injected field, use an Initializer instead.

## Initializers
Initializers are a (public) method annotated with @Initializer. When an ObjectFactory constructs an instance and performs all tasks on it (eg. Injection), it will call all initializer methods. This is useful as outlined above in Dependency Injection.

## Important Note
When creating a type that will be provided through an ObjectFactory, ensure that classes (and inner classes) are public and non-final. ObjectFactory will often create subclasses of the type declared as a static inner class through runtime bytecode manipulation, which will then be instantiated and returned rather than a direct instance of the type.

# Interceptors
Interceptors allow AOP to a certain extent within Java. This allows one to create method pipelines, effectively providing functionality to dynamically execute code before / after a method by placing an annotation on the method. Examples of interceptors can be found in the [interceptors package](https://github.com/UlfricProjects/dragoon/tree/develop/src/main/java/com/ulfric/dragoon/interceptors). To use an interceptor, you must add the interceptor annotation to your method declaration and ensure the method is public and non-final.
```java
@Asynchronous
public void doFoo()
{
    System.out.println("I am running on Thread " + Thread.currentThread().getName());
}
```
A call to doFoo() on an ObjectFactory provided instance displays that it is indeed executing on a different Thread.

# Beans
Beans allows conversion of interfaces to concrete Bean implementations dynamically created at runtime through bytecode manipulation. This allows the definition of Beans as interfaces rather than concrete classes, reducing semantics and allowing faster / simpler definition of Bean types. An instance can be obtained with `Beans.create(BeanInterface.class);`. 
Example:
```java
public interface BeanInterface {
    String getName();
    void setName(String name);
}

BeanInterface bean = Beans.create(BeanInterface.class);
```
One may then safely call getName / setName on the bean, and the value will be stored in a field `String name`.

# Modularity

## Containers
Containers hold Features, and maintain current state (i.e. enabled / disabled). Features can be installed with `Container#install(Class)`. The provided Class can either be a type that implements the Feature interface, or alternatively be converted to a Feature through the use of a FeatureWrapper.

## Features
Features maintain a state of enabled / disabled. Specific functions should be included within a Feature, for example listening for a certain event. Feature initialization should be achieved with Initializers, by creating a method `setup`, and adding the @Initializer annotation to it.

## Feature Wrappers
The goal of a FeatureWrapper is to convert an Object of a given type to a Feature. It is a BiFunction which takes parameters Feature (the requesting feature) and T - the instance to covert, and return a Feature. An example of a FeatureWrapper can be found in commons-spigot as [ListenerFeature](https://github.com/UlfricProjects/commons-spigot/blob/develop/src/main/java/com/ulfric/commons/spigot/listener/ListenerFeature.java), which is registered as a FeatureWrapper through `Container.registerFeatureWrapper(Listener.class, ListenerFeature::new);`.
