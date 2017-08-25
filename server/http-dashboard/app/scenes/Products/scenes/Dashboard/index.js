import React from 'react';
import _ from 'lodash';
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

    widget.id = this.props.dashboard.get('widgets').reduce((acc, item) => {
      return Number(item.get('id')) > acc ? Number(item.get('id')) : acc;
    }, 0) + 1;

    this.props.changeFormValue(FORMS.DASHBOARD, 'widgets', this.props.dashboard.get('widgets').push(fromJS(widget)));
  }

  handleWidgetsChange(widgets) {

    let updatedWidgets = this.props.dashboard.get('widgets').map((widget) => {
      // widget widget where i = widget.id (i is dashboard widget id and update updated fields)
      let data = _.find(widgets, (w) => Number(w.i) === Number(widget.get('id')));

      return fromJS({
        ...widget.toJS(),
        ...data,
      });
    });

    this.props.changeFormValue(FORMS.DASHBOARD, 'widgets', updatedWidgets);
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
