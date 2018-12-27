package cc.blynk.server.core.processors;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.rules.Rule;
import cc.blynk.server.core.processors.rules.actions.BaseAction;
import cc.blynk.server.core.processors.rules.actions.SetNumberPinAction;
import cc.blynk.server.core.processors.rules.value.FormulaValue;
import cc.blynk.server.core.processors.rules.value.ValueBase;
import cc.blynk.server.core.processors.rules.value.params.BackDeviceReferenceFormulaParam;
import cc.blynk.server.core.processors.rules.value.params.DeviceDataStreamFormulaParam;
import cc.blynk.server.core.processors.rules.value.params.DeviceReferenceFormulaParam;
import cc.blynk.server.core.processors.rules.value.params.FormulaParamBase;
import cc.blynk.server.core.processors.rules.value.params.SameDataStreamFormulaParam;
import cc.blynk.utils.NumberUtil;
import net.objecthunter.exp4j.Expression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 23.12.18.
 */
public class RuleEngineProcessor {

    private static final Logger log = LogManager.getLogger(RuleEngineProcessor.class);

    public RuleEngineProcessor() {
    }

    public void process(Organization org, Device device,
                        short pin, PinType pinType, String triggerValue) {
        if (org.ruleGroup != null) {
            double triggerValueParsed = NumberUtil.parseDouble(triggerValue);
            for (Rule rule : org.ruleGroup.rules) {
                if (rule.isValid(device.productId, pin, pinType, triggerValue, triggerValueParsed)) {
                    execute(org, rule, device, triggerValue);
                }
            }
        }
    }

    private void execute(Organization org, Rule rule, Device device,
                         String triggerValue) {
        BaseAction action = rule.action;
        if (action instanceof SetNumberPinAction) {
            SetNumberPinAction setNumberPinAction = (SetNumberPinAction) action;
            double resolvedValue = resolve(org, device, setNumberPinAction.pinValue, triggerValue);
            if (resolvedValue != NumberUtil.NO_RESULT) {
                device.updateValue(setNumberPinAction.targetDataStream, String.valueOf(resolvedValue));
            }
        }
    }

    private double resolve(Organization org, Device device, ValueBase value, String triggerValue) {
        if (value instanceof FormulaValue) {
            FormulaValue formulaValue = (FormulaValue) value;
            return resolveFormulaValue(org, device, formulaValue, triggerValue);
        } else {
            throw new RuntimeException("Not supported value passed for rule engine.");
        }
    }

    private double resolveFormulaValue(Organization org, Device device,
                                       FormulaValue formulaValue, String triggerValue) {
        //todo this is not optimal, we may cache expression per device
        Expression newExpressionCopy = new Expression(formulaValue.expression);

        for (Map.Entry<String, FormulaParamBase> entry : formulaValue.formulaParams.entrySet()) {
            FormulaParamBase formulaParam = entry.getValue();

            String resolvedValue = resolveParamValue(org, device, formulaParam, triggerValue);
            if (resolvedValue == null) {
                return NumberUtil.NO_RESULT;
            }

            String paramName = entry.getKey();
            newExpressionCopy.setVariable(paramName, NumberUtil.parseDouble(resolvedValue));
        }
        return newExpressionCopy.evaluate();
    }

    private String resolveParamValue(Organization org, Device device,
                                     FormulaParamBase formulaParamBase, String triggerValue) {
        if (formulaParamBase instanceof SameDataStreamFormulaParam) {
            return triggerValue;
        }
        if (formulaParamBase instanceof DeviceDataStreamFormulaParam) {
            DeviceDataStreamFormulaParam deviceDataStream = (DeviceDataStreamFormulaParam) formulaParamBase;
            return deviceDataStream.resolve(device);
        }
        if (formulaParamBase instanceof DeviceReferenceFormulaParam) {
            DeviceReferenceFormulaParam deviceReference = (DeviceReferenceFormulaParam) formulaParamBase;
            return deviceReference.resolve(org, device);
        }
        if (formulaParamBase instanceof BackDeviceReferenceFormulaParam) {
            BackDeviceReferenceFormulaParam backDeviceReference = (BackDeviceReferenceFormulaParam) formulaParamBase;
            return backDeviceReference.resolve(org, device);
        }
        throw new RuntimeException("Not supported formula parameter passed for rule engine.");
    }



}
