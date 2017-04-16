import React from 'react';
import FormItem from 'components/FormItem';
import Name from './components/Name';
import Value from './components/Value';
import './styles.less';
class Preview extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <FormItem offset={false}>
        <FormItem.Title>Preview</FormItem.Title>
        <FormItem.Content>
          <div className="product-metadata-item--preview">
            {this.props.children}
          </div>
        </FormItem.Content>
      </FormItem>
    );
  }
}

Preview.Name = Name;
Preview.Value = Value;

export default Preview;
