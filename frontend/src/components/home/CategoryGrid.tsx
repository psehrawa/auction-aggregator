import { Grid, Card, CardContent, Typography, Box } from '@mui/material'
import { 
  Computer, 
  DirectionsCar, 
  Weekend, 
  Build,
  Category as CategoryIcon,
  MoreHoriz 
} from '@mui/icons-material'
import { useNavigate } from 'react-router-dom'

const categories = [
  { id: 'electronics', name: 'Electronics', icon: Computer, color: '#1976d2' },
  { id: 'vehicles', name: 'Vehicles', icon: DirectionsCar, color: '#f44336' },
  { id: 'furniture', name: 'Furniture', icon: Weekend, color: '#ff9800' },
  { id: 'industrial', name: 'Industrial Equipment', icon: Build, color: '#4caf50' },
  { id: 'general', name: 'General Goods', icon: CategoryIcon, color: '#9c27b0' },
  { id: 'more', name: 'More Categories', icon: MoreHoriz, color: '#607d8b' },
]

const CategoryGrid = () => {
  const navigate = useNavigate()

  const handleCategoryClick = (categoryId: string) => {
    navigate(`/search?category=${categoryId}`)
  }

  return (
    <Grid container spacing={3}>
      {categories.map((category) => (
        <Grid item xs={6} sm={4} md={2} key={category.id}>
          <Card 
            sx={{ 
              cursor: 'pointer',
              transition: 'transform 0.2s',
              '&:hover': {
                transform: 'translateY(-4px)',
                boxShadow: 3,
              }
            }}
            onClick={() => handleCategoryClick(category.id)}
          >
            <CardContent sx={{ textAlign: 'center' }}>
              <Box sx={{ color: category.color, mb: 1 }}>
                <category.icon sx={{ fontSize: 48 }} />
              </Box>
              <Typography variant="body2" component="div">
                {category.name}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  )
}

export default CategoryGrid