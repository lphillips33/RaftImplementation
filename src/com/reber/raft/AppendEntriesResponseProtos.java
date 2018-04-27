// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: AppendEntriesResponse.proto

package com.reber.raft;

public final class AppendEntriesResponseProtos {
  private AppendEntriesResponseProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface AppendEntriesResponseOrBuilder extends
      // @@protoc_insertion_point(interface_extends:raft.AppendEntriesResponse)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional int32 term = 1;</code>
     */
    boolean hasTerm();
    /**
     * <code>optional int32 term = 1;</code>
     */
    int getTerm();

    /**
     * <code>optional bool success = 2;</code>
     */
    boolean hasSuccess();
    /**
     * <code>optional bool success = 2;</code>
     */
    boolean getSuccess();
  }
  /**
   * Protobuf type {@code raft.AppendEntriesResponse}
   */
  public  static final class AppendEntriesResponse extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:raft.AppendEntriesResponse)
      AppendEntriesResponseOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use AppendEntriesResponse.newBuilder() to construct.
    private AppendEntriesResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private AppendEntriesResponse() {
      term_ = 0;
      success_ = false;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private AppendEntriesResponse(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
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
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              term_ = input.readInt32();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              success_ = input.readBool();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.reber.raft.AppendEntriesResponseProtos.internal_static_raft_AppendEntriesResponse_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.reber.raft.AppendEntriesResponseProtos.internal_static_raft_AppendEntriesResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse.class, com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse.Builder.class);
    }

    private int bitField0_;
    public static final int TERM_FIELD_NUMBER = 1;
    private int term_;
    /**
     * <code>optional int32 term = 1;</code>
     */
    public boolean hasTerm() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional int32 term = 1;</code>
     */
    public int getTerm() {
      return term_;
    }

    public static final int SUCCESS_FIELD_NUMBER = 2;
    private boolean success_;
    /**
     * <code>optional bool success = 2;</code>
     */
    public boolean hasSuccess() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional bool success = 2;</code>
     */
    public boolean getSuccess() {
      return success_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, term_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBool(2, success_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, term_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(2, success_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse)) {
        return super.equals(obj);
      }
      com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse other = (com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse) obj;

      boolean result = true;
      result = result && (hasTerm() == other.hasTerm());
      if (hasTerm()) {
        result = result && (getTerm()
            == other.getTerm());
      }
      result = result && (hasSuccess() == other.hasSuccess());
      if (hasSuccess()) {
        result = result && (getSuccess()
            == other.getSuccess());
      }
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasTerm()) {
        hash = (37 * hash) + TERM_FIELD_NUMBER;
        hash = (53 * hash) + getTerm();
      }
      if (hasSuccess()) {
        hash = (37 * hash) + SUCCESS_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
            getSuccess());
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code raft.AppendEntriesResponse}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:raft.AppendEntriesResponse)
        com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.reber.raft.AppendEntriesResponseProtos.internal_static_raft_AppendEntriesResponse_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.reber.raft.AppendEntriesResponseProtos.internal_static_raft_AppendEntriesResponse_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse.class, com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse.Builder.class);
      }

      // Construct using com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        term_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        success_ = false;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.reber.raft.AppendEntriesResponseProtos.internal_static_raft_AppendEntriesResponse_descriptor;
      }

      public com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse getDefaultInstanceForType() {
        return com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse.getDefaultInstance();
      }

      public com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse build() {
        com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse buildPartial() {
        com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse result = new com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.term_ = term_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.success_ = success_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse) {
          return mergeFrom((com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse other) {
        if (other == com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse.getDefaultInstance()) return this;
        if (other.hasTerm()) {
          setTerm(other.getTerm());
        }
        if (other.hasSuccess()) {
          setSuccess(other.getSuccess());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int term_ ;
      /**
       * <code>optional int32 term = 1;</code>
       */
      public boolean hasTerm() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional int32 term = 1;</code>
       */
      public int getTerm() {
        return term_;
      }
      /**
       * <code>optional int32 term = 1;</code>
       */
      public Builder setTerm(int value) {
        bitField0_ |= 0x00000001;
        term_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 term = 1;</code>
       */
      public Builder clearTerm() {
        bitField0_ = (bitField0_ & ~0x00000001);
        term_ = 0;
        onChanged();
        return this;
      }

      private boolean success_ ;
      /**
       * <code>optional bool success = 2;</code>
       */
      public boolean hasSuccess() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional bool success = 2;</code>
       */
      public boolean getSuccess() {
        return success_;
      }
      /**
       * <code>optional bool success = 2;</code>
       */
      public Builder setSuccess(boolean value) {
        bitField0_ |= 0x00000002;
        success_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bool success = 2;</code>
       */
      public Builder clearSuccess() {
        bitField0_ = (bitField0_ & ~0x00000002);
        success_ = false;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:raft.AppendEntriesResponse)
    }

    // @@protoc_insertion_point(class_scope:raft.AppendEntriesResponse)
    private static final com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse();
    }

    public static com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<AppendEntriesResponse>
        PARSER = new com.google.protobuf.AbstractParser<AppendEntriesResponse>() {
      public AppendEntriesResponse parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new AppendEntriesResponse(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<AppendEntriesResponse> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<AppendEntriesResponse> getParserForType() {
      return PARSER;
    }

    public com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_raft_AppendEntriesResponse_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_raft_AppendEntriesResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\033AppendEntriesResponse.proto\022\004raft\"6\n\025A" +
      "ppendEntriesResponse\022\014\n\004term\030\001 \001(\005\022\017\n\007su" +
      "ccess\030\002 \001(\010B-\n\016com.reber.raftB\033AppendEnt" +
      "riesResponseProtos"
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
    internal_static_raft_AppendEntriesResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_raft_AppendEntriesResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_raft_AppendEntriesResponse_descriptor,
        new java.lang.String[] { "Term", "Success", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
