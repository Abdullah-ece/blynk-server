import React from 'react';
import propTypes from 'prop-types';
import {reduxForm} from 'redux-form';
import {Input, Item, ItemsGroup} from "components/UI";
import {
  DEVICES_SEARCH_FORM_NAME,
} from 'services/Devices';
import {
  Select,
} from 'antd';
import './styles.less';

@reduxForm({
  form: DEVICES_SEARCH_FORM_NAME,
  initialValues: {
    name: ''
  }
})
class DevicesSearch extends React.Component {

  static propTypes = {
    devicesSortValue: propTypes.string,
    devicesSortChange: propTypes.func,

    sortingOptions: propTypes.array,
  };

  render() {
    return (
      <div className="devices-search">
        <ItemsGroup>
          <Item>
            <Input style={{width: '100%'}} name="name"
                   placeholder="Search by device name"
                   mode="multiple"
                   autoComplete="off"
                   notFoundContent="Search is on development"/>
          </Item>
          <Item style={{width: '100px'}}>
            <Select style={{width: '100px', maxWidth: '100px', minWidth: '100px'}} optionLabelProp={'label'}
                    value={this.props.devicesSortValue}
                    onChange={this.props.devicesSortChange}
                    dropdownMatchSelectWidth={false}>
              {this.props.sortingOptions && this.props.sortingOptions.map((option) => (
                <Select.Option key={option.key}
                               label={option.label}>
                  {option.text}
                </Select.Option>
              ))}
            </Select>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default DevicesSearch;
