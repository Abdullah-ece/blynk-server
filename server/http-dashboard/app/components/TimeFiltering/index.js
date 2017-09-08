import React from 'react';
import {Field} from 'redux-form';
import {TIMELINE_TIME_FILTERS} from 'services/Devices';
import {TimeSelect, SpecificTimeSelect} from './components';
import './styles.less';

class TimeFiltering extends React.Component {

  static propTypes = {
    time: React.PropTypes.string,
    formValues: React.PropTypes.object,
  };

  render() {
    return (
      <div className="devices--device-dashboard--time-filtering">
        <Field name="time" component={TimeSelect}/>
        { this.props.formValues && this.props.formValues.time === TIMELINE_TIME_FILTERS.CUSTOM.key && (
          <Field name="customTime" component={SpecificTimeSelect}/>
        )}
      </div>

    );
  }

}

export default TimeFiltering;
