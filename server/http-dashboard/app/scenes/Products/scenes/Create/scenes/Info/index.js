import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {ProductInfoUpdateValues} from 'data/Product/actions';
import InfoForm from './components/InfoForm';

@connect(() => ({}), (dispatch) => ({
  updateValues: bindActionCreators(ProductInfoUpdateValues, dispatch)
}))
class Info extends React.Component {

  static propTypes = {
    updateValues: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.invalid = false;
  }

  onChange(values) {
    this.props.updateValues(values);
  }

  render() {

    return (
      <InfoForm onChange={this.onChange.bind(this)}/>
    );
  }
}

export default Info;
