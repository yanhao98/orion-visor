<template>
  <div class="cron-inner-config-list">
    <a-radio-group v-model="type">
      <div class="item">
        <a-radio :value="TypeEnum.every" v-bind="beforeRadioAttrs">每时</a-radio>
      </div>
      <div class="item">
        <a-radio :value="TypeEnum.range" v-bind="beforeRadioAttrs">区间</a-radio>
        <span>从</span>
        <a-input-number v-model="valueRange.start" v-bind="typeRangeAttrs" />
        <span>时 至</span>
        <a-input-number v-model="valueRange.end" v-bind="typeRangeAttrs" />
        <span>时</span>
      </div>
      <div class="item">
        <a-radio :value="TypeEnum.loop" v-bind="beforeRadioAttrs">循环</a-radio>
        <span>从</span>
        <a-input-number v-model="valueLoop.start" v-bind="typeLoopAttrs" />
        <span>时开始, 间隔</span>
        <a-input-number v-model="valueLoop.interval" v-bind="typeLoopAttrs" />
        <span>时</span>
      </div>
      <div class="item">
        <a-radio :value="TypeEnum.specify" v-bind="beforeRadioAttrs">指定</a-radio>
        <div class="list">
          <a-checkbox-group v-model="valueList">
            <a-grid :cols="12">
              <a-grid-item v-for="i in specifyRange" :key="i">
                <a-checkbox :value="i" v-bind="typeSpecifyAttrs">
                  {{ i }}
                </a-checkbox>
              </a-grid-item>
            </a-grid>
          </a-checkbox-group>
        </div>
      </div>
    </a-radio-group>
  </div>
</template>

<script lang="ts">
  import { defineComponent } from 'vue';
  import { useFormProps, useFormSetup, useFromEmits } from './use-mixin';

  export default defineComponent({
    name: 'HourForm',
    props: useFormProps({
      defaultValue: '*',
    }),
    emits: useFromEmits(),
    setup(props, context) {
      return useFormSetup(props, context, {
        defaultValue: '*',
        minValue: 0,
        maxValue: 23,
        valueRange: { start: 0, end: 23 },
        valueLoop: { start: 0, interval: 1 },
      });
    },
  });
</script>
