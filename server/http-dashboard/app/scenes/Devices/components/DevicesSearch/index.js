import React from 'react';
import propTypes from 'prop-types';
import {
  DEVICES_SORT
} from 'services/Devices';
import {
  Select,
  Input,
  Icon,
} from 'antd';
import './styles.less';

class DevicesSearch extends React.Component {

  static propTypes = {
    devicesSortValue: propTypes.string,
    devicesSortChange: propTypes.func,
  };

  render() {
    return (
      <div className="devices-search">
        <Input.Group compact>
          <Select style={{width: '100%'}} placeholder="Search..." mode="multiple"
                  notFoundContent="Search is on development"/>
          <Select style={{width: '100px', maxWidth: '100px', minWidth: '100px'}} optionLabelProp={'label'}
                  value={this.props.devicesSortValue}
                  onChange={this.props.devicesSortChange}
                  dropdownMatchSelectWidth={false}>
            <Select.Option key={DEVICES_SORT.REQUIRE_ATTENTION.key}
                           label={<span><Icon type="arrow-down"/> Attention</span>}>
              Require Attention ↓
            </Select.Option>
            <Select.Option key={DEVICES_SORT.AZ.key}
                           label={<span><Icon type="arrow-up"/> AZ</span>}>
              Alphabetical AZ
            </Select.Option>
            <Select.Option key={DEVICES_SORT.ZA.key}
                           label={<span><Icon type="arrow-down"/> ZA</span>}>
              Alphabetical ZA
            </Select.Option>
            <Select.Option key={DEVICES_SORT.DATE_ADDED_ASC.key}
                           label={<span><Icon type="arrow-down"/> Date</span>}>
              Date Added ↓
            </Select.Option>
            <Select.Option key={DEVICES_SORT.DATE_ADDED_DESC.key}
                           label={<span><Icon type="arrow-up"/> Date</span>}>
              Date Added ↑
            </Select.Option>
            <Select.Option key={DEVICES_SORT.LAST_REPORTED_ASC.key}
                           label={<span><Icon type="arrow-down"/> Last Reported</span>}>
              Last Reported ↓
            </Select.Option>
            <Select.Option key={DEVICES_SORT.LAST_REPORTED_DESC.key}
                           label={<span><Icon type="arrow-up"/> Last Reported</span>}>
              Last Reported ↑
            </Select.Option>
          </Select>
        </Input.Group>
      </div>
    );
  }

}

export default DevicesSearch;
