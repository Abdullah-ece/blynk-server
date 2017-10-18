import React from 'react';
import PropTypes from 'prop-types';
import {List} from 'immutable';
import Validation from 'services/Validation';
import {
  Button,
  Icon,
} from 'antd';
import {BarChartWidget} from 'components/Widgets/components';

import {MetadataSelect as Select} from 'components/Form';

import {reduxForm} from 'redux-form';

import './styles.less';

@reduxForm()
class Preview extends React.Component {

  static propTypes = {
    devicesList: PropTypes.instanceOf(List),

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

    widgetData: PropTypes.shape({
      id: PropTypes.number,
      w: PropTypes.number,
      h: PropTypes.number
    }).isRequired,

    chartData: PropTypes.shape({
      x: PropTypes.array.isRequired,
      y: PropTypes.array.isRequired,
    }).isRequired,

    devicesLoading: PropTypes.bool,

    submitting: PropTypes.bool,

    handleSubmit: PropTypes.func
  };

  renderLoading() {
    return (
      <div>
        <Icon type="loading" />
      </div>
    );
  }

  renderPreview() {
    let devicesOptions = [];

    this.props.devicesList.forEach((device) => {
      devicesOptions.push({
        key: String(device.get('id')),
        value: `${device.get('productName')} - ${String(device.get('name'))} - ${device.get('token')}`
      });
    });

    return (
      <div>
        <div>
          <Select name="deviceId"
                  values={devicesOptions}
                  placeholder="Please select device"
          validate={[Validation.Rules.required]}/>
        </div>

        <div className="widgets--widget bar-widget-preview">
          <BarChartWidget fetchRealData={false}
                          params={this.props.params}
                          data={this.props.widgetData}
                          fakeData={this.props.chartData}/>
        </div>

        <div>
          <Button type="submit" onClick={this.props.handleSubmit} loading={this.props.submitting}>
            Update Chart
          </Button>
        </div>

      </div>
    );
  }

  render() {

    if(this.props.devicesLoading || this.props.devicesList === null)
      return this.renderLoading();

    return this.renderPreview();
  }

}

export default Preview;
