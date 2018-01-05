import React from 'react';
import {
  Button,
  Icon
} from 'antd';
import PropTypes from 'prop-types';
import {
  WIDGET_TYPES,
  WIDGETS_PREDEFINED_OPTIONS,
} from 'services/Widgets';
import './styles.less';

class AddWidgetTools extends React.Component {

  static propTypes = {
    onWidgetAdd: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);

    this.handleBarWidgetAdd = this.handleWidgetAdd.bind(this, WIDGET_TYPES.BAR);
    this.handleLabelWidgetAdd = this.handleWidgetAdd.bind(this, WIDGET_TYPES.LABEL);
    this.handleLinearWidgetAdd = this.handleWidgetAdd.bind(this, WIDGET_TYPES.LINEAR);
  }

  handleWidgetAdd(type) {
    const widget = {
      ...WIDGETS_PREDEFINED_OPTIONS[type]
    };

    this.props.onWidgetAdd(widget);
  }

  render() {
    return (
      <div className="product-manage-dashboard--add-widget-tools">
        <div className="product-manage-dashboard--add-widget-tools-buttons">
          <Button.Group>
            <Button onClick={this.handleLabelWidgetAdd}><Icon type="laptop" style={{verticalAlign: 'middle'}}/></Button>
            <Button onClick={this.handleLinearWidgetAdd}><Icon type="area-chart"/></Button>
            <Button onClick={this.handleBarWidgetAdd}><Icon type="bar-chart"/></Button>
            <Button disabled={true}><Icon type="close"/></Button>
            <Button disabled={true}><Icon type="close"/></Button>
            <Button disabled={true}><Icon type="close"/></Button>
            <Button disabled={true}><Icon type="close"/></Button>
          </Button.Group>
        </div>
      </div>
    );
  }

}

export default AddWidgetTools;
