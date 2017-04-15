import React from 'react';
import './styles.less';
import Title from './components/Title';
import TitleGroup from './components/TitleGroup';
import Content from './components/Content';

class FormItem extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="form-item">
        {this.props.children}
      </div>
    );
  }
}

FormItem.Title = Title;
FormItem.TitleGroup = TitleGroup;
FormItem.Content = Content;

export default FormItem;
