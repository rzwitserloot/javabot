package javabot.javadoc;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.PrePersist;

@Entity(value = "fields", noClassnameStored = true)
@Indexes({
    @Index(fields = {@Field("javadocClassId"), @Field("upperName")}),
    @Index(fields = {@Field("apiId"), @Field("javadocClassId"), @Field("upperName")}),
})

public class JavadocField extends JavadocElement {
  @Id
  private ObjectId id;

  private ObjectId javadocClassId;
  private String name;
  private String upperName;
  private String parentClassName;

  private String type;

  public JavadocField() {
  }

  public JavadocField(final JavadocClass parent, final String fieldName, final String fieldType) {

    javadocClassId = parent.getId();
    name = fieldName;
    type = fieldType;
    setApiId(parent.getApiId());
    String url = parent.getDirectUrl() + "#" + name;
    setLongUrl(url);
    setDirectUrl(url);
    parentClassName = parent.toString();
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(final ObjectId methodId) {
    id = methodId;
  }

  public ObjectId getJavadocClassId() {
    return javadocClassId;
  }

  public void setJavadocClassId(final ObjectId javadocClassId) {
    this.javadocClassId = javadocClassId;
  }

  public void setJavadocClassId(final JavadocClass javadocClass) {
    this.javadocClassId = javadocClass.getId();
    parentClassName = javadocClass.toString();
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  @PrePersist
  public void uppers() {
    upperName = name.toUpperCase();
  }

  @Override
  public String toString() {
    return parentClassName + "#" + name + ":" + type;
  }
}