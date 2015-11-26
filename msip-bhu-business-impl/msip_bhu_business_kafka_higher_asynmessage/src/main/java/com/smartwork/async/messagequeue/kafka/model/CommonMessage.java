/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.smartwork.async.messagequeue.kafka.model;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class CommonMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"CommonMessage\",\"namespace\":\"com.smartwork.async.messagequeue.kafka.model\",\"fields\":[{\"name\":\"type\",\"type\":\"string\"},{\"name\":\"payload\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence type;
  @Deprecated public java.lang.CharSequence payload;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public CommonMessage() {}

  /**
   * All-args constructor.
   */
  public CommonMessage(java.lang.CharSequence type, java.lang.CharSequence payload) {
    this.type = type;
    this.payload = payload;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return type;
    case 1: return payload;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: type = (java.lang.CharSequence)value$; break;
    case 1: payload = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'type' field.
   */
  public java.lang.CharSequence getType() {
    return type;
  }

  /**
   * Sets the value of the 'type' field.
   * @param value the value to set.
   */
  public void setType(java.lang.CharSequence value) {
    this.type = value;
  }

  /**
   * Gets the value of the 'payload' field.
   */
  public java.lang.CharSequence getPayload() {
    return payload;
  }

  /**
   * Sets the value of the 'payload' field.
   * @param value the value to set.
   */
  public void setPayload(java.lang.CharSequence value) {
    this.payload = value;
  }

  /** Creates a new CommonMessage RecordBuilder */
  public static com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder newBuilder() {
    return new com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder();
  }
  
  /** Creates a new CommonMessage RecordBuilder by copying an existing Builder */
  public static com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder newBuilder(com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder other) {
    return new com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder(other);
  }
  
  /** Creates a new CommonMessage RecordBuilder by copying an existing CommonMessage instance */
  public static com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder newBuilder(com.smartwork.async.messagequeue.kafka.model.CommonMessage other) {
    return new com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder(other);
  }
  
  /**
   * RecordBuilder for CommonMessage instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CommonMessage>
    implements org.apache.avro.data.RecordBuilder<CommonMessage> {

    private java.lang.CharSequence type;
    private java.lang.CharSequence payload;

    /** Creates a new Builder */
    private Builder() {
      super(com.smartwork.async.messagequeue.kafka.model.CommonMessage.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.type)) {
        this.type = data().deepCopy(fields()[0].schema(), other.type);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.payload)) {
        this.payload = data().deepCopy(fields()[1].schema(), other.payload);
        fieldSetFlags()[1] = true;
      }
    }
    
    /** Creates a Builder by copying an existing CommonMessage instance */
    private Builder(com.smartwork.async.messagequeue.kafka.model.CommonMessage other) {
            super(com.smartwork.async.messagequeue.kafka.model.CommonMessage.SCHEMA$);
      if (isValidValue(fields()[0], other.type)) {
        this.type = data().deepCopy(fields()[0].schema(), other.type);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.payload)) {
        this.payload = data().deepCopy(fields()[1].schema(), other.payload);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'type' field */
    public java.lang.CharSequence getType() {
      return type;
    }
    
    /** Sets the value of the 'type' field */
    public com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder setType(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.type = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'type' field has been set */
    public boolean hasType() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'type' field */
    public com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder clearType() {
      type = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'payload' field */
    public java.lang.CharSequence getPayload() {
      return payload;
    }
    
    /** Sets the value of the 'payload' field */
    public com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder setPayload(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.payload = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'payload' field has been set */
    public boolean hasPayload() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'payload' field */
    public com.smartwork.async.messagequeue.kafka.model.CommonMessage.Builder clearPayload() {
      payload = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public CommonMessage build() {
      try {
        CommonMessage record = new CommonMessage();
        record.type = fieldSetFlags()[0] ? this.type : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.payload = fieldSetFlags()[1] ? this.payload : (java.lang.CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
