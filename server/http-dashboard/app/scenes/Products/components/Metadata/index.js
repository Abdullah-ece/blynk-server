import './styles.less';
import Item from './components/Item';
import ItemsList from './components/ItemsList';

import TextField from './components/TextField';
import NumberField from './components/NumberField';

const Metadata = {
  Item: Item,
  ItemsList: ItemsList,
  Fields: {
    TextField,
    NumberField
  }
};

export default Metadata;
