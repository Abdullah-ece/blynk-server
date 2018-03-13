import React from 'react';
import PropTypes from 'prop-types';
import {TimeFiltering} from 'components';
import {reduxForm} from 'redux-form';

@reduxForm()
class TimeFilteringFormWrapper extends React.Component {

  static propTypes = {
    formValues: PropTypes.object
  };

  render() {
    return (
      <TimeFiltering formValues={this.props.formValues}/>
    );
  }

}

export default TimeFilteringFormWrapper;
