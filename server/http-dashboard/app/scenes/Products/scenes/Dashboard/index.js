import React from 'react';
import {fromJS, Map} from 'immutable';

import {
  Dashboard
} from '../../components/ProductManage/components';

import {FORMS} from 'services/Products';

import {
  getFormValues,
  change,
} from 'redux-form';
import PropTypes from 'prop-types';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

@connect((state) => ({
  dashboard: fromJS(getFormValues(FORMS.DASHBOARD)(state) || {})
}), (dispatch) => ({
  changeFormValue: bindActionCreators(change, dispatch)
}))
class DashboardScene extends React.Component {

  static propTypes = {
    dashboard: PropTypes.instanceOf(Map),

    changeFormValue: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
    this.handleWidgetsChange = this.handleWidgetsChange.bind(this);
  }

  handleWidgetAdd(widget) {
    this.props.changeFormValue(FORMS.DASHBOARD, 'widgets', this.props.dashboard.get('widgets').push(fromJS(widget)));
  }

  handleWidgetsChange(widgets) {
    this.props.changeFormValue(FORMS.DASHBOARD, 'widgets', widgets);
  }

  render() {

    const widgets = this.props.dashboard.get('widgets');

    return (
      <Dashboard widgets={widgets}
                 onWidgetAdd={this.handleWidgetAdd}
                 onWidgetsChange={this.handleWidgetsChange}
      />
    );
  }

}

export default DashboardScene;
