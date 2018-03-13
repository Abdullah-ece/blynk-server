import React from 'react';
import {Fields, reduxForm} from 'redux-form';
import {TimeSelect, SpecificTimeSelect} from './components';
import PropTypes from 'prop-types';
import './styles.less';

@reduxForm()
class TimeFiltering extends React.Component {

  static propTypes = {
    time: PropTypes.string,
    options: PropTypes.array,
  };

  renderFields(props) {

    const isVisible = (timeValue) => {
      return props.options.some((option) => {

        return option.key === timeValue && option.isCustomTime;

      });
    };

    return (
      <div className="devices--device-dashboard--time-filtering">
        <TimeSelect options={props.options || []} input={props.time.input}/>
        <SpecificTimeSelect input={props.customTime.input} visible={isVisible(props.time.input.value)}/>
      </div>
    );
  }

  render() {
    const { options } = this.props;

    return (
      <div>
        <Fields names={['time', 'customTime']} component={this.renderFields} options={options}/>
      </div>

    );
  }

}

export default TimeFiltering;
