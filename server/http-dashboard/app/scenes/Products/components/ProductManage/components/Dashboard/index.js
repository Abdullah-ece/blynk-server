import React from 'react';
import {
  AddWidgetTools,
  Grid
} from './components';
import PropTypes from 'prop-types';
import {getNextId} from 'services/Products';
import {fromJS} from 'immutable';
import './styles.less';


class Dashboard extends React.Component {

  static propTypes = {
    fields: PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
  }

  handleWidgetDelete(id) {
    let fieldIndex = null;

    this.props.fields.getAll().forEach((field, index) => {
      if(Number(field.id) === Number(id))
        fieldIndex = index;
    });

    this.props.fields.remove(fieldIndex);
  }

  handleWidgetAdd(widget) {
    this.props.fields.push({
      ...widget,
      id: getNextId(this.props.fields.getAll()),
    });
  }

  render() {

    const widgets = fromJS(this.props.fields.map((prefix, index, fields) => {
      return {
        ...fields.get(index),
        fieldName: prefix,
      };
    }));

    const params = {
      id: 1
    };

    return (
      <div className="products-manage-dashboard">

        <AddWidgetTools onWidgetAdd={this.handleWidgetAdd}/>

        <Grid widgets={widgets}
              params={params}
              onWidgetDelete={this.handleWidgetDelete}
        />

      </div>
    );
  }

}

export default Dashboard;
