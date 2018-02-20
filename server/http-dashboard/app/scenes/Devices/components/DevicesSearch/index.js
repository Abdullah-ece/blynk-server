import React from 'react';
import propTypes from 'prop-types';
import {reduxForm} from 'redux-form';
import {connect} from 'react-redux';
import {Input, Item, ItemsGroup} from 'components/UI';
import { DEVICES_SEARCH_FORM_NAME } from 'services/Devices';
import { Select } from 'antd';
import './styles.less';

@connect(state => ({ smartSearch: state.Storage.deviceSmartSearch }))
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

    smartSearch: propTypes.bool
  };

  renderSortingOptions(){
    const { sortingOptions, devicesSortValue, devicesSortChange } = this.props;

    return <Select style={{width: '100px', maxWidth: '100px', minWidth: '100px'}}
                   optionLabelProp={'label'}
                   value={devicesSortValue}
                   onChange={devicesSortChange}
                   dropdownMatchSelectWidth={false}>
      {sortingOptions && sortingOptions.map(option => (
          <Select.Option key={option.key}
                         label={option.label}>
            {option.text}
          </Select.Option>
      ))}
    </Select>;
  }

  render() {
    const { smartSearch } = this.props;

    // slightly different layout when Smart Search are enabled
    if (smartSearch){
      return <div className="devices-search">
        <Select style={{width: 'calc(100% - 104px)', marginRight: 4}}
                name="name"
                mode="tags"
                placeholder="Smart Search" />
        {this.renderSortingOptions()}
      </div>;
    }

    return (
      <div className="devices-search">
        <ItemsGroup>
          <Item>
            <Input style={{width: '100%'}}
                   name="name"
                   mode="multiple"
                   placeholder="Search by device name"
                   autoComplete="off"
                   notFoundContent="Search is on development"/>
            </Item>
          <Item style={{width: '100px'}}>{this.renderSortingOptions()}</Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default DevicesSearch;
