import React from 'react';
import PropTypes from 'prop-types';
import {List} from 'immutable';
import Validation from 'services/Validation';
import {
  Button,
  Icon,
} from 'antd';

import Dotdotdot from 'react-dotdotdot';

import {LabelWidget} from 'components/Widgets/components';

import {MetadataSelect as Select} from 'components/Form';

import {reduxForm} from 'redux-form';

import {WIDGETS_LABEL_TEXT_ALIGNMENT} from 'services/Widgets';

import './styles.less';

@reduxForm()
class Preview extends React.Component {

  static propTypes = {
    devicesList: PropTypes.instanceOf(List),

    widgetData: PropTypes.shape({
      id: PropTypes.number,
      w: PropTypes.number,
      h: PropTypes.number
    }).isRequired,

    data: PropTypes.object,

    labelData: PropTypes.number,

    devicesLoading: PropTypes.bool,

    submitting: PropTypes.bool,

    handleSubmit: PropTypes.func,

    invalid: PropTypes.bool,
    pristine: PropTypes.bool
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

    const labelStyles = {};

    if(this.props.data.backgroundColor)
      labelStyles.background = '#'+this.props.data.backgroundColor;

    if(this.props.data.textColor)
      labelStyles.color = '#'+this.props.data.textColor;

    const getTextAlignStyle = (alignment) => {
      if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT)
        return 'left';

      if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.CENTER)
        return 'center';

      if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.RIGHT)
        return 'right';
    };

    labelStyles.textAlign = getTextAlignStyle(this.props.data.alignment);

    return (
      <div>
        <div>
          <Select name="deviceId"
                  values={devicesOptions}
                  placeholder="Choose device for data preview"
                  notFoundContent={devicesOptions.length > 0 ? "No Devices match search" : "Create at least one device to preview data"}
          validate={devicesOptions.length > 0 ? [Validation.Rules.required] : []}/>
        </div>

        <div className="widgets--widget label-widget-preview" style={labelStyles}>
          <div className="widgets--widget-label" style={{padding: 0}}>
            <Dotdotdot clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          </div>
          <LabelWidget fetchRealData={false}
                       data={this.props.data}
                       fakeData={this.props.labelData}
                       isChartPreview={true}/>
        </div>

        <div>
          <Button type="submit"
                  onClick={this.props.handleSubmit}
                  loading={this.props.submitting}
                  disabled={this.props.invalid || this.props.pristine || devicesOptions.length === 0}>

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
