import React from 'react';
import {
  Button,
  Icon
} from 'antd';
import PropTypes from 'prop-types';
import './styles.less';

class AddWidgetTools extends React.Component {

  static propTypes = {
    onWidgetAdd: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
  }

  handleWidgetAdd() {
    const widget = {
      x: 0,
      y: 0,
      w: 3,
      h: 2
    };

    this.props.onWidgetAdd(widget);
  }

  render() {
    return (
      <div className="product-manage-dashboard--add-widget-tools">
        <div className="product-manage-dashboard--add-widget-tools-title">+ Add New Widget:</div>
        <div className="product-manage-dashboard--add-widget-tools-buttons">
          <Button.Group>
            <Button onClick={this.handleWidgetAdd}><Icon type="area-chart"/></Button>
            <Button disabled={true}><Icon type="pie-chart"/></Button>
            <Button disabled={true}><Icon type="dot-chart"/></Button>
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
