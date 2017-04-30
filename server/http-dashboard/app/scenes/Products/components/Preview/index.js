import React from 'react';
import FormItem from 'components/FormItem';
import Name from './components/Name';
import Value from './components/Value';
import Unavailable from './components/Unavailable';
import './styles.less';
class Preview extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="product-metadata-item--preview">
        <FormItem offset={false}>
          <FormItem.Title>Preview</FormItem.Title>
          <FormItem.Content>
            <div className="product-metadata-item--preview-content">
              {this.props.children}
            </div>
          </FormItem.Content>
        </FormItem>
      </div>
    );
  }
}

Preview.Name = Name;
Preview.Value = Value;
Preview.Unavailable = Unavailable;

export default Preview;
