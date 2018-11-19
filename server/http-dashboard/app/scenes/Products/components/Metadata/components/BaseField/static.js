import React from 'react';
import Metadata from 'scenes/Products/components/Metadata';


class BaseFieldStatic extends React.Component {

  static propTypes = {
    role: React.PropTypes.array,
    icon: React.PropTypes.string
  };

  constructor(props) {
    super(props);

    if (!this.component) {
      throw Error('BaseFieldStatic children should have component method');
    }
  }

  DEFAULT_VALUE = 'No Value';

  render() {

    const {Item} = Metadata;
    return (
      <Item.Static preview={this.getPreviewValues()} role={this.props.role} icon={this.props.icon}>
        { this.component() }
      </Item.Static>
    );
  }
}

export default BaseFieldStatic;
