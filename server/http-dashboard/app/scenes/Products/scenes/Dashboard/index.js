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

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

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

    widget.y = this.props.dashboard.get('widgets').reduce((acc, item) => {
      return Number(item.get('y')) > acc ? Number(item.get('y')) : acc;
    }, 0) + 1;

    widget.name = widget.type + widget.id;
    this.props.changeFormValue(FORMS.DASHBOARD, 'widgets', this.props.dashboard.get('widgets').unshift(fromJS(widget) ));
  }

  handleWidgetsChange(widgets) {

    let updatedWidgets = widgets.map((widget) => {
      let data = this.props.dashboard.get('widgets').find((w) => Number(w.get('id')) === Number(widget.i));

      return fromJS({
        ...data.toJS(),
        ...widget
      });
    });

    this.props.changeFormValue(FORMS.DASHBOARD, 'widgets', updatedWidgets);
  }

  render() {

    const widgets = this.props.dashboard.get('widgets');

    return (
      <Dashboard widgets={widgets}
                 params={this.props.params}
                 onWidgetAdd={this.handleWidgetAdd}
                 onWidgetsChange={this.handleWidgetsChange}
      />
    );
  }

}

export default DashboardScene;
