// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: main/java/com/firecode/hadooptest/protobuf/helloword/HelloWord.proto

package com.firecode.hadooptest.protobuf.helloword;

public final class HelloWrodBuilder {
  private HelloWrodBuilder() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface HelloWordOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.firecode.hadooptest.protobuf.helloword.HelloWord)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int32 id = 1;</code>
     */
    boolean hasId();
    /**
     * <code>required int32 id = 1;</code>
     */
    int getId();

    /**
     * <code>required string str = 2;</code>
     */
    boolean hasStr();
    /**
     * <code>required string str = 2;</code>
     */
    java.lang.String getStr();
    /**
     * <code>required string str = 2;</code>
     */
    com.google.protobuf.ByteString
        getStrBytes();

    /**
     * <code>optional int32 opt = 3;</code>
     */
    boolean hasOpt();
    /**
     * <code>optional int32 opt = 3;</code>
     */
    int getOpt();
  }
  /**
   * Protobuf type {@code com.firecode.hadooptest.protobuf.helloword.HelloWord}
   */
  public static final class HelloWord extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:com.firecode.hadooptest.protobuf.helloword.HelloWord)
      HelloWordOrBuilder {
    // Use HelloWord.newBuilder() to construct.
    private HelloWord(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private HelloWord(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final HelloWord defaultInstance;
    public static HelloWord getDefaultInstance() {
      return defaultInstance;
    }

    public HelloWord getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private HelloWord(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              id_ = input.readInt32();
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              str_ = bs;
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              opt_ = input.readInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord.class, com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord.Builder.class);
    }

    public static com.google.protobuf.Parser<HelloWord> PARSER =
        new com.google.protobuf.AbstractParser<HelloWord>() {
      public HelloWord parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new HelloWord(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<HelloWord> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int ID_FIELD_NUMBER = 1;
    private int id_;
    /**
     * <code>required int32 id = 1;</code>
     */
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int32 id = 1;</code>
     */
    public int getId() {
      return id_;
    }

    public static final int STR_FIELD_NUMBER = 2;
    private java.lang.Object str_;
    /**
     * <code>required string str = 2;</code>
     */
    public boolean hasStr() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required string str = 2;</code>
     */
    public java.lang.String getStr() {
      java.lang.Object ref = str_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          str_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string str = 2;</code>
     */
    public com.google.protobuf.ByteString
        getStrBytes() {
      java.lang.Object ref = str_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        str_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int OPT_FIELD_NUMBER = 3;
    private int opt_;
    /**
     * <code>optional int32 opt = 3;</code>
     */
    public boolean hasOpt() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional int32 opt = 3;</code>
     */
    public int getOpt() {
      return opt_;
    }

    private void initFields() {
      id_ = 0;
      str_ = "";
      opt_ = 0;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasStr()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getStrBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt32(3, opt_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getStrBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, opt_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code com.firecode.hadooptest.protobuf.helloword.HelloWord}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.firecode.hadooptest.protobuf.helloword.HelloWord)
        com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWordOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord.class, com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord.Builder.class);
      }

      // Construct using com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        id_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        str_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        opt_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_descriptor;
      }

      public com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord getDefaultInstanceForType() {
        return com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord.getDefaultInstance();
      }

      public com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord build() {
        com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord buildPartial() {
        com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord result = new com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.str_ = str_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.opt_ = opt_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord) {
          return mergeFrom((com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord other) {
        if (other == com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord.getDefaultInstance()) return this;
        if (other.hasId()) {
          setId(other.getId());
        }
        if (other.hasStr()) {
          bitField0_ |= 0x00000002;
          str_ = other.str_;
          onChanged();
        }
        if (other.hasOpt()) {
          setOpt(other.getOpt());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasId()) {
          
          return false;
        }
        if (!hasStr()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int id_ ;
      /**
       * <code>required int32 id = 1;</code>
       */
      public boolean hasId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required int32 id = 1;</code>
       */
      public int getId() {
        return id_;
      }
      /**
       * <code>required int32 id = 1;</code>
       */
      public Builder setId(int value) {
        bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 id = 1;</code>
       */
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object str_ = "";
      /**
       * <code>required string str = 2;</code>
       */
      public boolean hasStr() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required string str = 2;</code>
       */
      public java.lang.String getStr() {
        java.lang.Object ref = str_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            str_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string str = 2;</code>
       */
      public com.google.protobuf.ByteString
          getStrBytes() {
        java.lang.Object ref = str_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          str_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string str = 2;</code>
       */
      public Builder setStr(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        str_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string str = 2;</code>
       */
      public Builder clearStr() {
        bitField0_ = (bitField0_ & ~0x00000002);
        str_ = getDefaultInstance().getStr();
        onChanged();
        return this;
      }
      /**
       * <code>required string str = 2;</code>
       */
      public Builder setStrBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        str_ = value;
        onChanged();
        return this;
      }

      private int opt_ ;
      /**
       * <code>optional int32 opt = 3;</code>
       */
      public boolean hasOpt() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional int32 opt = 3;</code>
       */
      public int getOpt() {
        return opt_;
      }
      /**
       * <code>optional int32 opt = 3;</code>
       */
      public Builder setOpt(int value) {
        bitField0_ |= 0x00000004;
        opt_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 opt = 3;</code>
       */
      public Builder clearOpt() {
        bitField0_ = (bitField0_ & ~0x00000004);
        opt_ = 0;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.firecode.hadooptest.protobuf.helloword.HelloWord)
    }

    static {
      defaultInstance = new HelloWord(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.firecode.hadooptest.protobuf.helloword.HelloWord)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\nDmain/java/com/firecode/hadooptest/prot" +
      "obuf/helloword/HelloWord.proto\022*com.fire" +
      "code.hadooptest.protobuf.helloword\"1\n\tHe" +
      "lloWord\022\n\n\002id\030\001 \002(\005\022\013\n\003str\030\002 \002(\t\022\013\n\003opt\030" +
      "\003 \001(\005B>\n*com.firecode.hadooptest.protobu" +
      "f.hellowordB\020HelloWrodBuilder"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_firecode_hadooptest_protobuf_helloword_HelloWord_descriptor,
        new java.lang.String[] { "Id", "Str", "Opt", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
