package lifecycle.adapter;

/**
 * 工作流事件适配器接口
 * 用于适配不同来源的事件
 */
public interface WorkflowEventAdapter {

    /**
     * 将原始事件转换为标准事件
     *
     * @param sourceEvent 原始事件对象
     * @return 标准事件
     */
    StandardEvent adaptToStandardEvent(Object sourceEvent);

    /**
     * 将标准事件转换为特定格式的事件
     *
     * @param standardEvent 标准事件
     * @param targetType 目标类型
     * @return 特定格式的事件
     */
    <T> T adaptFromStandardEvent(StandardEvent standardEvent, Class<T> targetType);

    /**
     * 验证事件是否有效
     *
     * @param standardEvent 标准事件
     * @return 是否有效
     */
    boolean validateEvent(StandardEvent standardEvent);

    /**
     * 获取事件元数据字典
     * 返回支持的元数据字段说明
     *
     * @return 元数据字典
     */
    EventMetadataDictionary getMetadataDictionary();

    /**
     * 事件元数据字典
     */
    class EventMetadataDictionary {
        /**
         * 支持的元数据字段列表
         */
        private MetadataField[] fields;

        public MetadataField[] getFields() {
            return fields;
        }

        public void setFields(MetadataField[] fields) {
            this.fields = fields;
        }

        /**
         * 元数据字段
         */
        public static class MetadataField {
            /**
             * 字段名
             */
            private String name;

            /**
             * 字段类型
             */
            private String type;

            /**
             * 是否必填
             */
            private boolean required;

            /**
             * 字段描述
             */
            private String description;

            /**
             * 示例值
             */
            private String example;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public boolean isRequired() {
                return required;
            }

            public void setRequired(boolean required) {
                this.required = required;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getExample() {
                return example;
            }

            public void setExample(String example) {
                this.example = example;
            }
        }
    }
}
