import React from 'react';

import {Form, Select} from 'antd';
import _ from 'lodash';

export default class SelectField extends React.Component {

  static propTypes = {
    dropdownClassName: React.PropTypes.any,
    dropdownStyle: React.PropTypes.any,
    displayError: React.PropTypes.any,
    values: React.PropTypes.any,
    onSelect: React.PropTypes.any,
    defaultValue: React.PropTypes.any,
    disabled: React.PropTypes.any,
    style: React.PropTypes.any,
    placeholder: React.PropTypes.any,
    mode: React.PropTypes.any,
    input: React.PropTypes.any,
    allowZero: React.PropTypes.bool,
    meta: React.PropTypes.any,
    dropdownMatchSelectWidth: React.PropTypes.bool,
    notFoundContent: React.PropTypes.string,
  };

  getFields(array) {
    let fields = [];
    array.forEach((item) => {
      fields.push(
        <Select.Option key={item.key} value={item.key} stringValue={item.value}>
          {item.value}
        </Select.Option>
      );
    });
    return fields;
  }

  getFieldsForGroup(groups, level) {
    let options = [];
    _.map(groups, (group, groupName) => {
      const groupPrefix = new Array(level).fill('-').join('');
      if (Array.isArray(group)) {
        options.push(<Select.OptGroup key={groupName}
                                      label={`${groupPrefix}${groupName}`}>{this.getFields(group)}</Select.OptGroup>);
      } else {
        options.push(<Select.OptGroup key={groupName}
                                      label={`${groupPrefix}${groupName}`}>{this.getFieldsForGroup(group, level + 2)}</Select.OptGroup>);
      }
    });
    return options;
  }

  getOptions(item) {
    let options;
    if (Array.isArray(item)) {
      options = this.getFields(item);
    } else if (typeof item === 'object') {
      options = this.getFieldsForGroup(item, 0);
    }

    return options;
  }

  valueFormat(value) {

    const {defaultValue, mode, allowZero} = this.props;

    let format = String;

    const formatArrayItem = (item) => String(item);

    if(mode === 'multiple' || mode === 'tags') {
      format = (value) => Array.isArray(value) ? value.map(formatArrayItem) : [];
    }

    if(Number(value) === 0 && allowZero === false) return undefined;

    return value !== undefined && value !== null ? format(value) : defaultValue ? format(defaultValue) : undefined;
  }

  render() {

    const {
      mode = false,
      dropdownMatchSelectWidth,
      notFoundContent,
      dropdownClassName,
      dropdownStyle,
      displayError = true,
      values,
      disabled = false,
      style,
      onSelect,
      placeholder,
      input,
      meta: {
        touched,
        error,
        warning
      }
    } = this.props;

    let validateStatus = 'success';
    let help = '';
    if (touched && displayError && error) {
      validateStatus = 'error';
      help = error || warning || '';
    }

    if (!touched && input.value && error) {
      validateStatus = 'error';
      help = error || warning || '';
    }

    return (
      <Form.Item validateStatus={validateStatus}
                 help={help}
                 style={style}>
        <Select
          mode={mode}
          {...input}
          disabled={disabled}
          dropdownMatchSelectWidth={dropdownMatchSelectWidth === undefined ? true : dropdownMatchSelectWidth}
          dropdownClassName={dropdownClassName}
          dropdownStyle={dropdownStyle}
          showSearch
          onSelect={onSelect}
          style={{width: '100%'}}
          onChange={input.onChange}
          placeholder={placeholder}
          notFoundContent={notFoundContent || null}
          optionFilterProp="children"
          value={this.valueFormat(input.value)}
          filterOption={(input, option) => (option.props.stringValue || option.props.value).toLowerCase().indexOf(input.toLowerCase()) >= 0}>
          { this.getOptions(values) }
        </Select>
      </Form.Item>
    );
  }
}
