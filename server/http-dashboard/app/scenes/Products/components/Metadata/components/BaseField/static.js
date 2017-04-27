import React from 'react';
import Metadata from 'scenes/Products/components/Metadata';


class BaseFieldStatic extends React.Component {

  DEFAULT_VALUE = 'No Value';

  static propTypes = {
    role: React.PropTypes.string
  };

  constructor(props) {
    super(props);

    if (!this.component) {
      throw Error('BaseFieldStatic children should have component method');
    }
  }

  render() {

    const {Item} = Metadata;

    return (
      <Item.Static preview={this.getPreviewValues()} role={this.props.role}>
        { this.component() }
      </Item.Static>
    );
  }
}

export default BaseFieldStatic;
